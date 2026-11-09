package ridehail;

import ridehail.exception.*;
import ridehail.model.*;
import ridehail.service.*;
import ridehail.util.FileStorage;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/** Console demo of the full project. */
public class Main {
    static void title(String t) { System.out.println("\n=== " + t + " ==="); }

    public static void main(String[] args) throws Exception {
        RegistrationService reg = new RegistrationService();
        RideService rideService = new RideService(reg);
        ReportService report = new ReportService(reg, rideService);
        FileStorage storage = new FileStorage("data");

        System.out.println("RIDE-HAILING APP SIMULATOR  |  Team 1  |  Roll Nos. 1 to 5");

        // ---------- Member 1: registration ----------
        title("MODULE 1: Registration (sample data)");
        String[][] riders = {
            {"Anil","9876500001","2","3"}, {"Bhavya","9876500002","8","1"}, {"Charan","9876500003","5","7"},
            {"Divya","9876500004","1","9"}, {"Eshwar","9876500005","9","9"}, {"Farah","9876500006","4","4"}};
        for (String[] r : riders)
            reg.registerRider(r[0], r[1], new Location(Double.parseDouble(r[2]), Double.parseDouble(r[3])));

        String[][] drivers = {
            {"Raju","9123400001","1","2","Auto","TS09AU1001"}, {"Suresh","9123400002","7","2","Auto","TS09AU1002"},
            {"Kiran","9123400003","3","5","Mini","TS09MN2001"}, {"Mahesh","9123400004","6","8","Mini","TS09MN2002"},
            {"Naveen","9123400005","9","8","Mini","TS09MN2003"}, {"Omkar","9123400006","2","8","Sedan","TS09SD3001"},
            {"Prakash","9123400007","5","5","Sedan","TS09SD3002"}, {"Ramesh","9123400008","8","3","Mini","TS09MN2004"}};
        for (String[] d : drivers)
            reg.registerDriver(d[0], d[1], new Location(Double.parseDouble(d[2]), Double.parseDouble(d[3])), d[4], d[5]);
        reg.printAll();

        title("MODULE 1: Wrong inputs (must be rejected)");
        String[][] bad = {{"Zoya","12345"}, {"","9999900001"}, {"Copy","9876500001"}};
        for (String[] b : bad) {
            try { reg.registerRider(b[0], b[1], new Location(0, 0)); }
            catch (InvalidRegistrationException e) { System.out.println("  REJECTED: " + e.getMessage()); }
        }
        try { reg.registerDriver("Bad","9000000001", new Location(0,0), "Bike", "XX1"); }
        catch (InvalidRegistrationException e) { System.out.println("  REJECTED: " + e.getMessage()); }

        // ---------- Member 2 + 3: matching and fare ----------
        title("MODULE 2 & 3: Single ride - nearest driver + fare");
        Rider anil = reg.findRider("R001");
        Ride r1 = rideService.requestRide(anil, new Location(10, 10), "Mini");
        System.out.println("  " + r1);
        System.out.println("  (Kiran at (3,5) is the nearest free Mini to Anil at (2,3))");
        rideService.completeRide(r1, 5);

        title("MODULE 3: Fare for 10 km, all vehicles, Normal vs Surge");
        FareCalculator n = new NormalFare(), s = new SurgeFare(1.5);
        for (Vehicle v : new Vehicle[]{new Auto("A"), new Mini("M"), new Sedan("S")})
            System.out.printf("  %-6s normal=Rs.%.2f   %s=Rs.%.2f%n", v.getType(), n.calculate(v, 10), s.getName(), s.calculate(v, 10));

        // ---------- Member 4: concurrency ----------
        title("MODULE 4: 6 riders request a Mini at the SAME time (only 4 Minis exist)");
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger ok = new AtomicInteger(), fail = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        List<RideRequestTask> tasks = new ArrayList<>();
        int i = 1;
        for (Rider r : reg.getRiders()) {
            RideRequestTask t = new RideRequestTask(rideService, r, new Location(r.getLocation().getX() + 3, r.getLocation().getY() - 2 < 0 ? 1 : r.getLocation().getY() - 2),
                                                    "Mini", go, ok, fail);
            tasks.add(t);
            Thread th = new Thread(t, "Thread-" + i++);
            threads.add(th); th.start();
        }
        go.countDown();
        for (Thread th : threads) th.join();
        System.out.println("  Confirmed=" + ok + "  Rejected=" + fail);

        Set<String> used = new HashSet<>(); boolean clash = false;
        for (RideRequestTask t : tasks)
            if (t.getResult() != null && !used.add(t.getResult().getDriver().getId())) clash = true;
        System.out.println("  Any driver given to two riders? " + (clash ? "YES (BUG!)" : "NO - synchronization works"));

        for (RideRequestTask t : tasks) if (t.getResult() != null) rideService.completeRide(t.getResult(), 4);

        // a few more rides for history
        title("More rides + ratings");
        Rider divya = reg.findRider("R004"), eshwar = reg.findRider("R005");
        Ride r2 = rideService.requestRide(divya, new Location(6, 2), "Sedan");   rideService.completeRide(r2, 5);
        Ride r3 = rideService.requestRide(eshwar, new Location(3, 1), "Auto");   rideService.completeRide(r3, 3);
        System.out.println("  " + r2); System.out.println("  " + r3);

        title("WRONG INPUT: no driver available");
        try {
            rideService.requestRide(anil, new Location(5, 5), "Sedan");
            Ride a = rideService.requestRide(reg.findRider("R002"), new Location(5, 5), "Sedan");
            Ride b = rideService.requestRide(reg.findRider("R003"), new Location(5, 5), "Sedan");   // both sedans busy now
            System.out.println("  unexpected: " + b);
        } catch (NoDriverAvailableException e) { System.out.println("  REJECTED: " + e.getMessage()); }
        try { rideService.completeRide(r2, 9); }
        catch (IllegalArgumentException e) { System.out.println("  Ignored invalid rating: " + e.getMessage()); }
        // free the sedans taken above
        for (Ride r : rideService.getRides()) if (r.getStatus() == RideStatus.ONGOING) rideService.completeRide(r, 4);

        // ---------- Member 5 ----------
        title("MODULE 5: Ride history");   report.printHistory();
        title("MODULE 5: Driver earnings and ratings");   report.printDriverEarnings();
        title("MODULE 5: Summary");   report.printSummary();

        title("MODULE 5: File storage (save, reload into fresh objects)");
        storage.saveAll(reg, rideService);
        RegistrationService reg2 = new RegistrationService();
        RideService rs2 = new RideService(reg2);
        boolean loaded = storage.loadAll(reg2, rs2);
        System.out.println("  Reloaded=" + loaded + "  riders=" + reg2.getRiders().size()
            + "  drivers=" + reg2.getDrivers().size() + "  rides=" + rs2.getRides().size());
    }
}
