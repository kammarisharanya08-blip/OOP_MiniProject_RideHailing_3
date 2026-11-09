package ridehail.model;

/** Abstract parent of Rider and Driver (abstraction + inheritance). */
public abstract class User {
    private final String id;
    private String name;
    private String phone;
    private Location location;

    protected User(String id, String name, String phone, Location location) {
        this.id = id; this.name = name; this.phone = phone; this.location = location;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public synchronized Location getLocation() { return location; }
    public synchronized void setLocation(Location l) { this.location = l; }
    public void setName(String n) { this.name = n; }
    public void setPhone(String p) { this.phone = p; }

    public abstract String getRole();

    @Override public String toString() {
        return String.format("%-5s %-8s %-10s %-12s %s", id, getRole(), name, phone, getLocation());
    }
}
