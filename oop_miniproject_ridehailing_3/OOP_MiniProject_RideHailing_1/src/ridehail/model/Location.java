package ridehail.model;

/** A point on a city grid. 1 unit = 1 km. */
public class Location {
    private final double x, y;
    public Location(double x, double y) { this.x = x; this.y = y; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double distanceTo(Location o) { return Math.hypot(x - o.x, y - o.y); }
    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}
