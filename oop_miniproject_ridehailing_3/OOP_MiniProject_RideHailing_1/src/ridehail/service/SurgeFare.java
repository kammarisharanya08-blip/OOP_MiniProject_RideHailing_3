package ridehail.service;

import ridehail.model.Vehicle;

/** Higher fare when demand is high: normal fare x multiplier. */
public class SurgeFare extends NormalFare {
    private final double multiplier;
    public SurgeFare(double multiplier) { this.multiplier = multiplier; }
    public double getMultiplier() { return multiplier; }
    @Override public double calculate(Vehicle v, double km) {
        return Math.round(super.calculate(v, km) * multiplier * 100.0) / 100.0;
    }
    @Override public String getName() { return "Surge x" + multiplier; }
}
