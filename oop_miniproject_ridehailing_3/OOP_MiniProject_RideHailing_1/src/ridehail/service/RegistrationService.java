package ridehail.service;

import ridehail.exception.InvalidRegistrationException;
import ridehail.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** MEMBER 1 MODULE: rider and driver registration (stored in HashMaps, listed with Iterator). */
public class RegistrationService {
    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private final Map<String, Driver> drivers = new LinkedHashMap<>();
    private final Set<String> phones = new HashSet<>();
    private final AtomicInteger riderSeq = new AtomicInteger(0);
    private final AtomicInteger driverSeq = new AtomicInteger(0);

    private void validate(String name, String phone) throws InvalidRegistrationException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidRegistrationException("Name cannot be empty.");
        if (phone == null || !phone.matches("[6-9]\\d{9}"))
            throw new InvalidRegistrationException("Invalid phone '" + phone + "': need 10 digits starting with 6-9.");
        if (phones.contains(phone))
            throw new InvalidRegistrationException("Phone " + phone + " is already registered.");
    }

    public synchronized Rider registerRider(String name, String phone, Location loc) throws InvalidRegistrationException {
        validate(name, phone);
        Rider r = new Rider(String.format("R%03d", riderSeq.incrementAndGet()), name.trim(), phone, loc);
        riders.put(r.getId(), r);
        phones.add(phone);
        return r;
    }

    public synchronized Driver registerDriver(String name, String phone, Location loc, String vehicleType, String plate)
            throws InvalidRegistrationException {
        validate(name, phone);
        if (plate == null || plate.trim().isEmpty())
            throw new InvalidRegistrationException("Vehicle number plate is required.");
        Vehicle v;
        try { v = Vehicle.create(vehicleType, plate.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { throw new InvalidRegistrationException(e.getMessage()); }
        Driver d = new Driver(String.format("D%03d", driverSeq.incrementAndGet()), name.trim(), phone, loc, v);
        drivers.put(d.getId(), d);
        phones.add(phone);
        return d;
    }

    /** Used by file loading: keep the stored id and move the counters forward. */
    public synchronized void addLoadedRider(Rider r) {
        riders.put(r.getId(), r); phones.add(r.getPhone());
        riderSeq.accumulateAndGet(Integer.parseInt(r.getId().substring(1)), Math::max);
    }
    public synchronized void addLoadedDriver(Driver d) {
        drivers.put(d.getId(), d); phones.add(d.getPhone());
        driverSeq.accumulateAndGet(Integer.parseInt(d.getId().substring(1)), Math::max);
    }

    public synchronized Rider findRider(String id) { return riders.get(id); }
    public synchronized Driver findDriver(String id) { return drivers.get(id); }
    public synchronized List<Rider> getRiders() { return new ArrayList<>(riders.values()); }
    public synchronized List<Driver> getDrivers() { return new ArrayList<>(drivers.values()); }

    /** Prints all users using an explicit Iterator. */
    public void printAll() {
        System.out.println("  --- Riders ---");
        for (Iterator<Rider> it = getRiders().iterator(); it.hasNext();) System.out.println("  " + it.next());
        System.out.println("  --- Drivers ---");
        for (Iterator<Driver> it = getDrivers().iterator(); it.hasNext();) System.out.println("  " + it.next());
    }
}
