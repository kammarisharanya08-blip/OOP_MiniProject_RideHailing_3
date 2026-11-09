package ridehail.service;

import ridehail.exception.NoDriverAvailableException;
import ridehail.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MEMBER 2: nearest free driver matching.
 * MEMBER 4: the matching method is synchronized, so two riders can never get the same driver.
 */
public class RideService {
    private static final double SURGE_THRESHOLD = 0.5;   // 50% or more drivers busy => surge
    private static final double SURGE_MULTIPLIER = 1.5;
    private static final double DRIVER_SHARE = 0.80;     // driver keeps 80% of the fare

    private final RegistrationService registry;
    private final List<Ride> rides = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger rideSeq = new AtomicInteger(0);
    private final FareCalculator normal = new NormalFare();
    private final FareCalculator surge = new SurgeFare(SURGE_MULTIPLIER);

    public RideService(RegistrationService registry) { this.registry = registry; }

    /** Critical section: find the nearest free driver, reserve him and price the ride. */
    public synchronized Ride requestRide(Rider rider, Location drop, String vehicleType)
            throws NoDriverAvailableException {
        Location pickup = rider.getLocation();
        List<Driver> all = registry.getDrivers();

        Driver best = null; double bestDist = Double.MAX_VALUE; int busy = 0;
        for (Driver d : all) {
            if (!d.isAvailable()) { busy++; continue; }
            if (!d.getVehicle().getType().equalsIgnoreCase(vehicleType)) continue;
            double dist = d.getLocation().distanceTo(pickup);
            if (dist < bestDist) { bestDist = dist; best = d; }
        }
        if (best == null)
            throw new NoDriverAvailableException("No free " + vehicleType + " driver for " + rider.getName());

        best.setAvailable(false);                         // reserve atomically
        busy++;
        boolean isSurge = !all.isEmpty() && (double) busy / all.size() >= SURGE_THRESHOLD;
        FareCalculator fc = isSurge ? surge : normal;

        double km = Math.round(pickup.distanceTo(drop) * 10.0) / 10.0;
        double fare = fc.calculate(best.getVehicle(), km);
        Ride ride = new Ride(String.format("RD%03d", rideSeq.incrementAndGet()),
                             rider, best, pickup, drop, km, fare, isSurge);
        rides.add(ride);
        return ride;
    }

    /** Finish the ride: free the driver, move him to the drop point, pay him, store the rating. */
    public synchronized void completeRide(Ride ride, int rating) {
        if (ride.getStatus() == RideStatus.COMPLETED) return;
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1 to 5");
        Driver d = ride.getDriver();
        ride.setStatus(RideStatus.COMPLETED);
        ride.setRating(rating);
        d.addRating(rating);
        d.addEarnings(Math.round(ride.getFare() * DRIVER_SHARE * 100.0) / 100.0);
        d.setLocation(ride.getDrop());
        d.setAvailable(true);
        ride.getRider().setLocation(ride.getDrop());
        ride.getRider().incrementRides();
    }

    public List<Ride> getRides() { synchronized (rides) { return new ArrayList<>(rides); } }
    public void addLoadedRide(Ride r) {
        rides.add(r);
        rideSeq.accumulateAndGet(Integer.parseInt(r.getId().substring(2)), Math::max);
    }
}
