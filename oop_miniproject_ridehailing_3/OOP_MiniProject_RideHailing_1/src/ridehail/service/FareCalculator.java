package ridehail.service;

import ridehail.model.Vehicle;

/** Member 3: fare strategy interface. */
public interface FareCalculator {
    double calculate(Vehicle vehicle, double distanceKm);
    String getName();
}
