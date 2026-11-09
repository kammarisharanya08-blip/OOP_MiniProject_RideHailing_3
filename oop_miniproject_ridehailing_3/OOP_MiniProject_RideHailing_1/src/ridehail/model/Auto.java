package ridehail.model;
public class Auto extends Vehicle {
    public Auto(String plate) { super(plate); }
    @Override public String getType() { return "Auto"; }
    @Override public double getBaseFare() { return 25; }
    @Override public double getRatePerKm() { return 12; }
}
