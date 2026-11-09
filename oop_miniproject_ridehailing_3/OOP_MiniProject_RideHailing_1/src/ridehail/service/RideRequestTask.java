package ridehail.service;

import ridehail.exception.NoDriverAvailableException;
import ridehail.model.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/** MEMBER 4: one rider's request running in its own thread. */
public class RideRequestTask implements Runnable {
    private final RideService service;
    private final Rider rider;
    private final Location drop;
    private final String vehicleType;
    private final CountDownLatch startSignal;
    private final AtomicInteger confirmed, failed;
    private volatile Ride result;

    public RideRequestTask(RideService s, Rider r, Location drop, String type,
                           CountDownLatch start, AtomicInteger confirmed, AtomicInteger failed) {
        this.service = s; this.rider = r; this.drop = drop; this.vehicleType = type;
        this.startSignal = start; this.confirmed = confirmed; this.failed = failed;
    }
    public Ride getResult() { return result; }

    @Override public void run() {
        try {
            startSignal.await();                          // all threads start together
            result = service.requestRide(rider, drop, vehicleType);
            confirmed.incrementAndGet();
            System.out.printf("  [%-8s] %-7s CONFIRMED  driver=%-7s fare=Rs.%.2f %s%n",
                Thread.currentThread().getName(), rider.getName(), result.getDriver().getName(),
                result.getFare(), result.isSurge() ? "(SURGE)" : "");
        } catch (NoDriverAvailableException e) {
            failed.incrementAndGet();
            System.out.printf("  [%-8s] %-7s REJECTED   %s%n", Thread.currentThread().getName(), rider.getName(), e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
