# OOP_MiniProject_RideHailing_1
**Ride-Hailing App Simulator (Ola / Uber style)** - Team 1 (Roll Nos. 1 to 5) - console version, MNR University, OOP (Java)

## Problem
Many riders request rides at the same time. The app must give each rider the nearest free driver, never give one driver to two riders, and charge a higher fare when demand is high.

## Solution
Riders and drivers register. A ride request is matched with the nearest free driver of the chosen vehicle type inside a `synchronized` method. Fare = base + rate x km, using a `FareCalculator` interface (`NormalFare`, `SurgeFare`). Surge (x1.5) applies when 50% or more of the drivers are busy. History, earnings and ratings are stored in CSV files.

## Packages
| Package | Classes |
|---|---|
| `ridehail.model` | User (abstract), Rider, Driver, Vehicle (abstract), Auto, Mini, Sedan, Ride, RideStatus (enum), Location |
| `ridehail.service` | RegistrationService, RideService, RideRequestTask (Runnable), FareCalculator (interface), NormalFare, SurgeFare, ReportService |
| `ridehail.exception` | NoDriverAvailableException, InvalidRegistrationException |
| `ridehail.util` | FileStorage |

## Team modules
| Member | Module | Files |
|---|---|---|
| 1 (Roll 1) | Rider and driver registration | RegistrationService, User, Rider, Driver, Vehicle + subclasses, InvalidRegistrationException |
| 2 | Ride request and nearest free driver matching | RideService.requestRide |
| 3 | Fare by distance, vehicle type and surge (interface) | FareCalculator, NormalFare, SurgeFare |
| 4 | Simultaneous requests (threads + synchronization) | RideRequestTask, synchronized methods in RideService |
| 5 | History, earnings, ratings, file storage | ReportService, FileStorage |

## How to run
```
javac -d out $(find src -name "*.java")
java -cp out ridehail.Main        # console demo (sample data, threads, wrong inputs)
```
Output of the console run is in `output.txt`. UML diagram: `UML_Class_Diagram.png`.

## Java concepts used
Classes/objects, constructors; encapsulation, inheritance, overriding, 5 packages; abstract class (User, Vehicle), interface (FareCalculator), custom exceptions, try-catch-finally (FileStorage), file handling (CSV); ArrayList, HashMap, HashSet, Iterator, Comparator; threads with synchronization (RideRequestTask, RideService); enum (RideStatus); Swing with event handling (RideHailGUI).
