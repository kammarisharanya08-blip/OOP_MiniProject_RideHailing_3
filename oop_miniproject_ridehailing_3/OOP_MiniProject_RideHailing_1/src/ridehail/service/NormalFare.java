package ridehail.service;

import ridehail.model.Vehicle;

public class NormalFare implements FareCalculator {
    @Override public double calculate(Vehicle v, double km) {
        return Math.round((v.getBaseFare() + v.getRatePerKm() * km) * 100.0) / 100.0;
    }
    @Override public String getName() { return "Normal"; }
}
