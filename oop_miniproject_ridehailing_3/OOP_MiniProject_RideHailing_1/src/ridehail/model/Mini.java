package ridehail.model;
public class Mini extends Vehicle {
    public Mini(String plate) { super(plate); }
    @Override public String getType() { return "Mini"; }
    @Override public double getBaseFare() { return 40; }
    @Override public double getRatePerKm() { return 14; }
}
