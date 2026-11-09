package ridehail.model;

public class Rider extends User {
    private int ridesTaken;
    public Rider(String id, String name, String phone, Location loc) { super(id, name, phone, loc); }
    public synchronized int getRidesTaken() { return ridesTaken; }
    public synchronized void incrementRides() { ridesTaken++; }
    @Override public String getRole() { return "Rider"; }
}
