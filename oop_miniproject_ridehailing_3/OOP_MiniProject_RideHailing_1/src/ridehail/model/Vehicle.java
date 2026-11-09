package ridehail.model;

/** Abstract vehicle. Auto, Mini and Sedan override the rates (polymorphism). */
public abstract class Vehicle {
    private final String plate;
    protected Vehicle(String plate) { this.plate = plate; }
    public String getPlate() { return plate; }
    public abstract String getType();
    public abstract double getBaseFare();
    public abstract double getRatePerKm();

    public static Vehicle create(String type, String plate) {
        switch (type) {
            case "Auto":  return new Auto(plate);
            case "Mini":  return new Mini(plate);
            case "Sedan": return new Sedan(plate);
            default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
    @Override public String toString() { return getType() + "[" + plate + "]"; }
}
