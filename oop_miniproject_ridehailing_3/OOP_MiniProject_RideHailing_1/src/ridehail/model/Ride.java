package ridehail.model;

public class Ride {
    private final String id;
    private final Rider rider;
    private final Driver driver;
    private final Location pickup, drop;
    private final double distanceKm, fare;
    private final boolean surge;
    private RideStatus status = RideStatus.ONGOING;
    private int rating;

    public Ride(String id, Rider rider, Driver driver, Location pickup, Location drop,
                double distanceKm, double fare, boolean surge) {
        this.id = id; this.rider = rider; this.driver = driver; this.pickup = pickup; this.drop = drop;
        this.distanceKm = distanceKm; this.fare = fare; this.surge = surge;
    }
    public String getId() { return id; }
    public Rider getRider() { return rider; }
    public Driver getDriver() { return driver; }
    public Location getPickup() { return pickup; }
    public Location getDrop() { return drop; }
    public double getDistanceKm() { return distanceKm; }
    public double getFare() { return fare; }
    public boolean isSurge() { return surge; }
    public synchronized RideStatus getStatus() { return status; }
    public synchronized void setStatus(RideStatus s) { status = s; }
    public synchronized int getRating() { return rating; }
    public synchronized void setRating(int r) { rating = r; }

    @Override public String toString() {
        return String.format("%-5s %-8s -> %-8s %-6s %5.1f km  Rs.%7.2f %-6s %-9s rating=%d",
            id, rider.getName(), driver.getName(), driver.getVehicle().getType(), distanceKm, fare,
            surge ? "SURGE" : "normal", getStatus(), getRating());
    }
}
