package ridehail.model;
public class Sedan extends Vehicle {
    public Sedan(String plate) { super(plate); }
    @Override public String getType() { return "Sedan"; }
    @Override public double getBaseFare() { return 60; }
    @Override public double getRatePerKm() { return 18; }
}
