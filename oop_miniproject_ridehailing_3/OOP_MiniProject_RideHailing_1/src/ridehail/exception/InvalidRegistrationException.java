package ridehail.exception;

/** Thrown for bad registration input (wrong phone, duplicate user, empty name...). */
public class InvalidRegistrationException extends Exception {
    public InvalidRegistrationException(String message) { super(message); }
}
