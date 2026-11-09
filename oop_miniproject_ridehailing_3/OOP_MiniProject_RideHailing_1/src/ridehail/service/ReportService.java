package ridehail.service;

import ridehail.model.*;

import java.util.*;

/** MEMBER 5: ride history, driver earnings and ratings. */
public class ReportService {
    private final RegistrationService registry;
    private final RideService rides;
    public ReportService(RegistrationService reg, RideService rides) { this.registry = reg; this.rides = rides; }

    public void printHistory() {
        System.out.println("  Ride history:");
        for (Ride r : rides.getRides()) System.out.println("  " + r);
    }

    public void printDriverEarnings() {
        List<Driver> list = registry.getDrivers();
        list.sort(Comparator.comparingDouble(Driver::getEarnings).reversed());   // Comparator sort
        System.out.printf("  %-5s %-8s %-6s %12s %8s %7s%n", "ID", "Driver", "Type", "Earnings(Rs)", "Rating", "Rides");
        for (Driver d : list)
            System.out.printf("  %-5s %-8s %-6s %12.2f %8.2f %7d%n", d.getId(), d.getName(),
                d.getVehicle().getType(), d.getEarnings(), d.getAverageRating(), d.getRatingCount());
    }

    public void printSummary() {
        double total = 0; int surge = 0, done = 0;
        for (Ride r : rides.getRides()) {
            total += r.getFare(); if (r.isSurge()) surge++;
            if (r.getStatus() == RideStatus.COMPLETED) done++;
        }
        System.out.printf("  Total rides=%d  completed=%d  surge rides=%d  total fare=Rs.%.2f%n",
            rides.getRides().size(), done, surge, total);
    }
}
