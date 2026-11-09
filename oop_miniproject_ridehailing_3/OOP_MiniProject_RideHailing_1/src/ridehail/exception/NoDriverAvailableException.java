package ridehail.exception;

/** Thrown when no free driver of the requested vehicle type exists. */
public class NoDriverAvailableException extends Exception {
    public NoDriverAvailableException(String message) { super(message); }
}
