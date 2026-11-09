package ridehail.model;

import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private final Vehicle vehicle;
    private boolean available = true;
    private double earnings;
    private final List<Integer> ratings = new ArrayList<>();

    public Driver(String id, String name, String phone, Location loc, Vehicle vehicle) {
        super(id, name, phone, loc);
        this.vehicle = vehicle;
    }
    public Vehicle getVehicle() { return vehicle; }
    public synchronized boolean isAvailable() { return available; }
    public synchronized void setAvailable(boolean a) { available = a; }
    public synchronized double getEarnings() { return earnings; }
    public synchronized void addEarnings(double amt) { earnings += amt; }
    public synchronized void addRating(int r) { ratings.add(r); }
    public synchronized int getRatingCount() { return ratings.size(); }
    public synchronized double getAverageRating() {
        if (ratings.isEmpty()) return 0;
        int s = 0; for (int r : ratings) s += r;
        return (double) s / ratings.size();
    }
    public synchronized void restoreStats(double earn, List<Integer> rs) { earnings = earn; ratings.clear(); ratings.addAll(rs); }
    public synchronized List<Integer> getRatings() { return new ArrayList<>(ratings); }
    @Override public String getRole() { return "Driver"; }
    @Override public String toString() { return super.toString() + "  " + vehicle; }
}
