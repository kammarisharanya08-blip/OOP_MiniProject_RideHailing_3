package ridehail.util;

import ridehail.model.*;
import ridehail.service.*;

import java.io.*;
import java.util.*;

/** MEMBER 5: save and load riders, drivers and rides as CSV files. */
public class FileStorage {
    private final File dir;
    public FileStorage(String dirName) { dir = new File(dirName); dir.mkdirs(); }

    public void saveAll(RegistrationService reg, RideService rides) {
        PrintWriter w = null;
        try {
            w = new PrintWriter(new FileWriter(new File(dir, "riders.csv")));
            w.println("id,name,phone,x,y");
            for (Rider r : reg.getRiders())
                w.println(r.getId() + "," + r.getName() + "," + r.getPhone() + "," + r.getLocation().getX() + "," + r.getLocation().getY());
            w.close();

            w = new PrintWriter(new FileWriter(new File(dir, "drivers.csv")));
            w.println("id,name,phone,x,y,type,plate,earnings,ratings");
            for (Driver d : reg.getDrivers()) {
                StringBuilder rs = new StringBuilder();
                for (int x : d.getRatings()) rs.append(rs.length() == 0 ? "" : "|").append(x);
                w.println(d.getId() + "," + d.getName() + "," + d.getPhone() + "," + d.getLocation().getX() + ","
                    + d.getLocation().getY() + "," + d.getVehicle().getType() + "," + d.getVehicle().getPlate()
                    + "," + d.getEarnings() + "," + rs);
            }
            w.close();

            w = new PrintWriter(new FileWriter(new File(dir, "rides.csv")));
            w.println("id,riderId,driverId,px,py,dx,dy,km,fare,surge,status,rating");
            for (Ride r : rides.getRides())
                w.println(r.getId() + "," + r.getRider().getId() + "," + r.getDriver().getId() + ","
                    + r.getPickup().getX() + "," + r.getPickup().getY() + "," + r.getDrop().getX() + "," + r.getDrop().getY()
                    + "," + r.getDistanceKm() + "," + r.getFare() + "," + r.isSurge() + "," + r.getStatus() + "," + r.getRating());
        } catch (IOException e) {
            System.out.println("  [!] Could not save data: " + e.getMessage());
        } finally {
            if (w != null) w.close();
            System.out.println("  [i] Save finished -> " + dir.getPath());
        }
    }

    public boolean loadAll(RegistrationService reg, RideService rides) {
        File fr = new File(dir, "riders.csv"), fd = new File(dir, "drivers.csv"), fx = new File(dir, "rides.csv");
        if (!fr.exists() || !fd.exists() || !fx.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(fr))) {
            br.readLine(); String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                reg.addLoadedRider(new Rider(p[0], p[1], p[2], new Location(Double.parseDouble(p[3]), Double.parseDouble(p[4]))));
            }
        } catch (IOException | RuntimeException e) { System.out.println("  [!] riders.csv: " + e); return false; }

        try (BufferedReader br = new BufferedReader(new FileReader(fd))) {
            br.readLine(); String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",", -1);
                Driver d = new Driver(p[0], p[1], p[2], new Location(Double.parseDouble(p[3]), Double.parseDouble(p[4])),
                                      Vehicle.create(p[5], p[6]));
                List<Integer> rs = new ArrayList<>();
                if (p.length > 8 && !p[8].isEmpty()) for (String s : p[8].split("\\|")) rs.add(Integer.parseInt(s));
                d.restoreStats(Double.parseDouble(p[7]), rs);
                reg.addLoadedDriver(d);
            }
        } catch (IOException | RuntimeException e) { System.out.println("  [!] drivers.csv: " + e); return false; }

        try (BufferedReader br = new BufferedReader(new FileReader(fx))) {
            br.readLine(); String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                Ride r = new Ride(p[0], reg.findRider(p[1]), reg.findDriver(p[2]),
                    new Location(Double.parseDouble(p[3]), Double.parseDouble(p[4])),
                    new Location(Double.parseDouble(p[5]), Double.parseDouble(p[6])),
                    Double.parseDouble(p[7]), Double.parseDouble(p[8]), Boolean.parseBoolean(p[9]));
                r.setStatus(RideStatus.valueOf(p[10])); r.setRating(Integer.parseInt(p[11]));
                rides.addLoadedRide(r);
            }
        } catch (IOException | RuntimeException e) { System.out.println("  [!] rides.csv: " + e); return false; }
        return true;
    }
}
