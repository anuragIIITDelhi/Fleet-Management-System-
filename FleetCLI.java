import java.util.*;
//import java.io.*;

class FleetCLI {
    private FleetManager fleetManager;
    private Scanner scanner;

    public FleetCLI() {
        this.fleetManager = new FleetManager();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== FLEET MANAGEMENT SYSTEM ===");
        System.out.println("Assignment 2: Collections & File I/O");
        showMainMenu();
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Add Vehicle");
            System.out.println("2. Remove Vehicle");
            System.out.println("3. Display All Vehicles");
            System.out.println("4. Check Fuel Status");
            System.out.println("5. Refuel All Vehicles");
            System.out.println("6. Start Journey");
            System.out.println("7. Generate Report A2");
            System.out.println("8. Sort the Vehicles ");
            System.out.println("9. File Operations");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1: addVehicle(); break;
                    case 2: removeVehicle(); break;
                    case 3: displayAllVehicles(); break;
                    case 4: checkFuelStatus(); break;
                    case 5: refuelAll(); break;
                    case 6: startJourney(); break;
                    case 7: generateEnhancedReport(); break;
                    case 8: showSortingMenu(); break;
                    case 9: showFileMenu(); break;
                    case 10:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showSortingMenu() {
        while (true) {
            System.out.println("\n=== SORTING & ANALYSIS ===");
            System.out.println("1. Sort by Max Speed (Fastest First)");
            System.out.println("2. Sort by Max Speed (Slowest First)");
            System.out.println("3. Sort by Model Name (A-Z)");
            System.out.println("4. Sort by Model Name (Z-A)");
            System.out.println("5. Show Fastest & Slowest Vehicles");
            System.out.println("6. Show Unique Models");
            System.out.println("7. Show Alphabetical Order");
            System.out.println("8. Sort by Fuel Efficiency Most Efficient First");
            System.out.println("9. Sort by Fuel Efficiency least Efficient First");

            System.out.println("10. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        fleetManager.sortByMaxSpeed(false);
                        fleetManager.displayAllVehicles();
                        break;
                    case 2:
                        fleetManager.sortByMaxSpeed(true);
                        fleetManager.displayAllVehicles();
                        break;
                    case 3:
                        fleetManager.sortByModelName(true);
                        fleetManager.displayAllVehicles();
                        break;
                    case 4:
                        fleetManager.sortByModelName(false);
                        fleetManager.displayAllVehicles();
                        break;
                    case 5: fleetManager.displayFastestAndSlowest(); break;
                    case 6: fleetManager.displayUniqueModels(); break;
                    case 7: fleetManager.displayAlphabeticalOrder(); break;
                    case 8: 
                            fleetManager.sortByFuelEfficiency(true);
                            fleetManager.displayAllVehicles();
                            break;
                    case 9:
                            fleetManager.sortByFuelEfficiency(false);
                            fleetManager.displayAllVehicles();
                            break;
                    case 10: return;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showFileMenu() {
        while (true) {
            System.out.println("\n=== FILE OPERATIONS ===");
            System.out.println("1. Save Fleet to File (with fuel, passengers, cargo)");
            System.out.println("2. Load Fleet from File (with fuel, passengers, cargo)");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: saveFleet(); break;
                    case 2: loadFleet(); break;
                    case 3: return;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addVehicle() {
        try {
            System.out.println("\n=== ADD VEHICLE ===");
            System.out.println("Select vehicle type:");
            System.out.println("1. Car\n2. Truck\n3. Bus\n4. Airplane\n5. CargoShip");
            System.out.print("Enter choice: ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter model: ");
            String model = scanner.nextLine();
            System.out.print("Enter max speed: ");
            double maxSpeed = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter current mileage: ");
            double mileage = Double.parseDouble(scanner.nextLine());

            Vehicle vehicle = null;

            switch (typeChoice) {
                case 1:
                    System.out.print("Enter number of wheels: ");
                    int wheels = Integer.parseInt(scanner.nextLine());
                    vehicle = new Car(id, model, maxSpeed, mileage, wheels);
                    break;
                case 2:
                    System.out.print("Enter number of wheels: ");
                    wheels = Integer.parseInt(scanner.nextLine());
                    vehicle = new Truck(id, model, maxSpeed, mileage, wheels);
                    break;
                case 3:
                    System.out.print("Enter number of wheels: ");
                    wheels = Integer.parseInt(scanner.nextLine());
                    vehicle = new Bus(id, model, maxSpeed, mileage, wheels);
                    break;
                case 4:
                    System.out.print("Enter max altitude: ");
                    double altitude = Double.parseDouble(scanner.nextLine());
                    vehicle = new Airplane(id, model, maxSpeed, mileage, altitude);
                    break;
                case 5:
                    System.out.print("Has sail? (true/false): ");
                    boolean hasSail = Boolean.parseBoolean(scanner.nextLine());
                    vehicle = new CargoShip(id, model, maxSpeed, mileage, hasSail);
                    break;
                default:
                    System.out.println("Invalid vehicle type.");
                    return;
            }

            fleetManager.addVehicle(vehicle);

        } catch (Exception e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
        }
    }

    private void removeVehicle() {
        try {
            if (fleetManager.isFleetEmpty()) {
                System.out.println("Fleet is empty. No vehicles to remove.");
                return;
            }
            
            System.out.print("\nEnter vehicle ID to remove: ");
            String id = scanner.nextLine();
            fleetManager.removeVehicle(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayAllVehicles() {
        fleetManager.displayAllVehicles();
    }

    private void checkFuelStatus() {
        fleetManager.displayFuelStatus();
    }

    private void refuelAll() {
        try {
            if (fleetManager.isFleetEmpty()) {
                System.out.println("Fleet is empty. No vehicles to refuel.");
                return;
            }
            
            System.out.print("\nEnter fuel amount to add to each vehicle: ");
            double amount = Double.parseDouble(scanner.nextLine());
            fleetManager.refuelAll(amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void startJourney() {
        try {
            if (fleetManager.isFleetEmpty()) {
                System.out.println("Fleet is empty. No vehicles to start journey with.");
                return;
            }
            
            System.out.print("\nEnter journey distance (km): ");
            double distance = Double.parseDouble(scanner.nextLine());
            fleetManager.startCompleteJourney(distance, scanner);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void generateEnhancedReport() {
        System.out.println("\n" + fleetManager.generateEnhancedReport());
    }

    private void saveFleet() {
        try {
            if (fleetManager.isFleetEmpty()) {
                System.out.println("Fleet is empty. Nothing to save.");
                return;
            }
            
            System.out.print("\nEnter filename to save: ");
            String filename = scanner.nextLine();
            fleetManager.saveToFile(filename);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadFleet() {
        try {
            System.out.print("\nEnter filename to load: ");
            String filename = scanner.nextLine();
            fleetManager.loadFromFile(filename);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}