import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

class FleetManager {
    private List<Vehicle> fleet = new ArrayList<>();
    private Set<String> uniqueModels = new HashSet<>();
    private TreeSet<Vehicle> vehiclesByName = new TreeSet<>(Comparator.comparing(Vehicle::getModel));

    private final Comparator<Vehicle> speedComparator = Comparator.comparingDouble(Vehicle::getMaxSpeed);
    private final Comparator<Vehicle> modelComparator = Comparator.comparing(Vehicle::getModel);
    private final Comparator<Vehicle> efficiencyComparator = Comparator.comparingDouble(Vehicle::calculateFuelEfficiency);

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        if (v == null) {
            throw new InvalidOperationException("Vehicle cannot be null");
        }

        for (Vehicle vehicle : fleet) {
            if (vehicle.getId().equals(v.getId())) {
                throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId());
            }
        }

        fleet.add(v);
        uniqueModels.add(v.getModel());
        vehiclesByName.add(v);

        System.out.println("Vehicle added successfully!");
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidOperationException("Vehicle ID cannot be null or empty");
        }

        if (fleet.isEmpty()) {
            throw new InvalidOperationException("Fleet is empty. No vehicles to remove.");
        }

        Iterator<Vehicle> iterator = fleet.iterator();
        Vehicle removedVehicle = null;

        while (iterator.hasNext()) {
            Vehicle v = iterator.next();
            if (v.getId().equals(id)) {
                removedVehicle = v;
                iterator.remove();
                break;
            }
        }

        if (removedVehicle == null) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        }

        updateCollectionsAfterRemoval(removedVehicle);
        System.out.println("Vehicle removed successfully!");
    }

    private void updateCollectionsAfterRemoval(Vehicle removedVehicle) {
        boolean modelStillExists = false;
        for (Vehicle v : fleet) {
            if (v.getModel().equals(removedVehicle.getModel())) {
                modelStillExists = true;
                break;
            }
        }
        if (!modelStillExists) {
            uniqueModels.remove(removedVehicle.getModel());
        }

        vehiclesByName.remove(removedVehicle);
    }

    public void refuelAll(double amount) throws InvalidOperationException {
        if (fleet.isEmpty()) {
            throw new InvalidOperationException("Fleet is empty. No vehicles to refuel.");
        }
        
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel amount must be positive.");
        }

        int refueledCount = 0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                try {
                    ((FuelConsumable) v).refuel(amount);
                    refueledCount++;
                } catch (InvalidOperationException e) {
                    System.out.println("Could not refuel " + v.getId() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Successfully refueled " + refueledCount + " vehicles!");
    }

    public boolean checkFuelForJourney(double distance) {
        if (fleet.isEmpty()) {
            System.out.println("Fleet is empty. No vehicles to check fuel for.");
            return false;
        }

        boolean allVehiclesHaveFuel = true;

        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                double requiredFuel = distance / v.calculateFuelEfficiency();
                if (fc.getFuelLevel() < requiredFuel) {
                    System.out.println("WARNING: " + v.getId() + " has insufficient fuel. Needs: " +
                            String.format("%.1f", requiredFuel) + "L, Has: " +
                            String.format("%.1f", fc.getFuelLevel()) + "L");
                    allVehiclesHaveFuel = false;
                }
            }
        }

        return allVehiclesHaveFuel;
    }

    public void startCompleteJourney(double distance, Scanner scanner) {
        if (fleet.isEmpty()) {
            System.out.println("Fleet is empty. No vehicles to start journey with.");
            return;
        }

        System.out.println("\n=== STARTING JOURNEY OF " + distance + " KM ===");

        if (!checkFuelForJourney(distance)) {
            System.out.println("\nSome vehicles have insufficient fuel!");
            System.out.print("Do you want to continue anyway? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("yes") && !response.equals("y")) {
                System.out.println("Journey cancelled. Please refuel vehicles first.");
                return;
            }
        }

        System.out.println("\n--- LOADING CARGO ---");
        for (Vehicle v : fleet) {
            if (v instanceof CargoCarrier) {
                CargoCarrier cargoVehicle = (CargoCarrier) v;
                System.out.print("Enter cargo weight (kg) for " + v.getId() + " (" + v.getModel() + "): ");
                try {
                    double cargoWeight = Double.parseDouble(scanner.nextLine());
                    cargoVehicle.loadCargo(cargoWeight);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        System.out.println("\n--- BOARDING PASSENGERS ---");
        for (Vehicle v : fleet) {
            if (v instanceof PassengerCarrier) {
                PassengerCarrier passengerVehicle = (PassengerCarrier) v;
                System.out.print("Enter passenger count for " + v.getId() + " (" + v.getModel() + "): ");
                try {
                    int passengers = Integer.parseInt(scanner.nextLine());
                    passengerVehicle.boardPassengers(passengers);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        System.out.println("\n--- JOURNEY SUMMARY ---");
        System.out.println("Vehicle ID\tModel\t\tType\t\tPassengers\tCargo\t\tFuel Status");
        System.out.println("--------------------------------------------------------------------------------");
        for (Vehicle v : fleet) {
            String passengersInfo = "N/A";
            String cargoInfo = "N/A";
            String fuelStatus = "N/A";

            if (v instanceof PassengerCarrier) {
                PassengerCarrier pc = (PassengerCarrier) v;
                passengersInfo = pc.getCurrentPassengers() + "/" + pc.getPassengerCapacity();
            }

            if (v instanceof CargoCarrier) {
                CargoCarrier cc = (CargoCarrier) v;
                cargoInfo = String.format("%.1f/%.1f kg", cc.getCurrentCargo(), cc.getCargoCapacity());
            }

            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                double requiredFuel = distance / v.calculateFuelEfficiency();
                fuelStatus = String.format("%.1f/%.1f L", fc.getFuelLevel(), requiredFuel);
                if (fc.getFuelLevel() < requiredFuel) {
                    fuelStatus += " (LOW)";
                }
            }

            System.out.printf("%-10s\t%-10s\t%-10s\t%-10s\t%-12s\t%-15s\n",
                    v.getId(), v.getModel(), v.getClass().getSimpleName(), passengersInfo, cargoInfo, fuelStatus);
        }

        System.out.println("\n--- EXECUTING JOURNEY ---");
        int successful = 0;
        int failed = 0;

        for (Vehicle v : fleet) {
            try {
                v.move(distance);
                successful++;
            } catch (Exception e) {
                System.out.println("Journey failed for " + v.getId() + ": " + e.getMessage());
                failed++;
            }
        }

        System.out.println("\n--- UNLOADING CARGO ---");
        for (Vehicle v : fleet) {
            if (v instanceof CargoCarrier) {
                CargoCarrier cargoVehicle = (CargoCarrier) v;
                double currentCargo = cargoVehicle.getCurrentCargo();
                if (currentCargo > 0) {
                    System.out.print("Unload cargo from " + v.getId() + " (" + v.getModel() + "). Enter weight (kg): ");
                    try {
                        double unloadWeight = Double.parseDouble(scanner.nextLine());
                        cargoVehicle.unloadCargo(unloadWeight);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("\n--- DISEMBARKING PASSENGERS ---");
        for (Vehicle v : fleet) {
            if (v instanceof PassengerCarrier) {
                PassengerCarrier passengerVehicle = (PassengerCarrier) v;
                int currentPassengers = passengerVehicle.getCurrentPassengers();
                if (currentPassengers > 0) {
                    System.out.print("Disembark passengers from " + v.getId() + " (" + v.getModel() + "). Enter count: ");
                    try {
                        int disembarkCount = Integer.parseInt(scanner.nextLine());
                        passengerVehicle.disembarkPassengers(disembarkCount);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("\n=== JOURNEY COMPLETED ===");
        System.out.println("Successful: " + successful + " vehicles");
        System.out.println("Failed: " + failed + " vehicles");
    }

    public void displayFuelStatus() {
        if (fleet.isEmpty()) {
            System.out.println("Fleet is empty. No vehicles to display fuel status for.");
            return;
        }

        System.out.println("\n=== CURRENT FUEL STATUS ===");
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                System.out.println(v.getId() + " (" + v.getModel() + "): " +
                        String.format("%.1f", fc.getFuelLevel()) + " L");
            } else {
                System.out.println(v.getId() + " (" + v.getModel() + "): No fuel system");
            }
        }
    }

    // Sorting methods
    public void sortByMaxSpeed(boolean ascending) {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles to sort.");
            return;
        }
        if (ascending) {
            Collections.sort(fleet, speedComparator);
        } else {
            Collections.sort(fleet, speedComparator.reversed());
        }
        System.out.println("Sorted by max speed " + (ascending ? "(ascending)" : "(descending)"));
    }

    public void sortByModelName(boolean ascending) {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles to sort.");
            return;
        }
        if (ascending) {
            Collections.sort(fleet, modelComparator);
        } else {
            Collections.sort(fleet, modelComparator.reversed());
        }
        System.out.println("Sorted by model name " + (ascending ? "(A-Z)" : "(Z-A)"));
    }
    

    // ========================Sort by fuel efficency -============================

    public void sortByFuelEfficiency(boolean ascending) {
    if (fleet.isEmpty()) {
        System.out.println("No vehicles to sort.");
        return;
    }
    if (ascending) {
        Collections.sort(fleet, efficiencyComparator);
    } else {
        Collections.sort(fleet, efficiencyComparator.reversed());
    }
    System.out.println("Sorted by fuel efficiency " + 
        (ascending ? "(most efficient first)" : "(least efficient first)"));
    }

    // Data analysis methods
    public void displayFastestAndSlowest() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            return;
        }

        Vehicle fastest = Collections.max(fleet, speedComparator);
        Vehicle slowest = Collections.min(fleet, speedComparator);

        System.out.println("\n=== SPEED ANALYSIS ===");
        System.out.println("Fastest: " + fastest.getModel() + " (" + fastest.getMaxSpeed() + " km/h)");
        System.out.println("Slowest: " + slowest.getModel() + " (" + slowest.getMaxSpeed() + " km/h)");
    }

    public void displayUniqueModels() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            return;
        }

        System.out.println("\n=== UNIQUE MODELS ===");
        System.out.println("Total unique models: " + uniqueModels.size());
        for (String model : uniqueModels) {
            System.out.println("- " + model);
        }
    }

    public void displayAlphabeticalOrder() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            return;
        }

        System.out.println("\n=== VEHICLES IN ALPHABETICAL ORDER ===");
        for (Vehicle v : vehiclesByName) {
            System.out.println(v.getModel() + " (" + v.getId() + ")");
        }
    }

    public void displayAllVehicles() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            return;
        }

        System.out.println("\n=== ALL VEHICLES ===");
        for (int i = 0; i < fleet.size(); i++) {
            System.out.print((i + 1) + ". ");
            fleet.get(i).displayInfo();
        }
    }

    // Enhanced File I/O with proper exception handling
    public void saveToFile(String filename) {
        if (fleet.isEmpty()) {
            System.out.println("Fleet is empty. Nothing to save.");
            return;
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("Type,ID,Model,MaxSpeed,CurrentMileage,FuelLevel,Passengers,Cargo,AdditionalInfo");

            for (Vehicle v : fleet) {
                String type = v.getClass().getSimpleName();
                String additionalInfo = "";
                double fuelLevel = 0.0;
                int passengers = 0;
                double cargo = 0.0;

                // Get fuel level
                if (v instanceof FuelConsumable) {
                    fuelLevel = ((FuelConsumable) v).getFuelLevel();
                }

                // Get passenger count
                if (v instanceof PassengerCarrier) {
                    passengers = ((PassengerCarrier) v).getCurrentPassengers();
                }

                // Get cargo weight
                if (v instanceof CargoCarrier) {
                    cargo = ((CargoCarrier) v).getCurrentCargo();
                }

                // Get additional info based on vehicle type
                if (v instanceof LandVehicle) {
                    additionalInfo = "Wheels:" + ((LandVehicle) v).getNumWheels();
                } else if (v instanceof AirVehicle) {
                    additionalInfo = "Altitude:" + ((AirVehicle) v).getMaxAltitude();
                } else if (v instanceof WaterVehicle) {
                    additionalInfo = "Sail:" + ((WaterVehicle) v).hasSail();
                }

                String line = String.join(",",
                        type,
                        v.getId(),
                        v.getModel(),
                        String.valueOf(v.getMaxSpeed()),
                        String.valueOf(v.getCurrentMileage()),
                        String.valueOf(fuelLevel),
                        String.valueOf(passengers),
                        String.valueOf(cargo),
                        additionalInfo
                );
                writer.println(line);
            }
            System.out.println("Fleet saved to " + filename + " with " + fleet.size() + " vehicles!");
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void loadFromFile(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            
            // Clear current fleet
            fleet.clear();
            uniqueModels.clear();
            vehiclesByName.clear();

            String line;
            boolean firstLine = true;
            int loadedCount = 0;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length < 6) continue;

                try {
                    String type = data[0];
                    String id = data[1];
                    String model = data[2];
                    double maxSpeed = Double.parseDouble(data[3]);
                    double mileage = Double.parseDouble(data[4]);
                    double fuelLevel = data.length > 5 ? Double.parseDouble(data[5]) : 0.0;
                    int passengers = data.length > 6 ? Integer.parseInt(data[6]) : 0;
                    double cargo = data.length > 7 ? Double.parseDouble(data[7]) : 0.0;
                    String additionalInfo = data.length > 8 ? data[8] : "";

                    Vehicle vehicle = null;

                    switch (type) {
                        case "Car":
                            int carWheels = extractNumber(additionalInfo, 4);
                            vehicle = new Car(id, model, maxSpeed, mileage, carWheels, fuelLevel, passengers);
                            break;
                        case "Truck":
                            int truckWheels = extractNumber(additionalInfo, 6);
                            vehicle = new Truck(id, model, maxSpeed, mileage, truckWheels, fuelLevel, cargo);
                            break;
                        case "Bus":
                            int busWheels = extractNumber(additionalInfo, 6);
                            vehicle = new Bus(id, model, maxSpeed, mileage, busWheels, fuelLevel, passengers, cargo);
                            break;
                        case "Airplane":
                            double altitude = extractDouble(additionalInfo, 10000.0);
                            vehicle = new Airplane(id, model, maxSpeed, mileage, altitude, fuelLevel, passengers, cargo);
                            break;
                        case "CargoShip":
                            boolean hasSail = extractBoolean(additionalInfo, false);
                            vehicle = new CargoShip(id, model, maxSpeed, mileage, hasSail, fuelLevel, cargo);
                            break;
                    }

                    if (vehicle != null) {
                        fleet.add(vehicle);
                        uniqueModels.add(vehicle.getModel());
                        vehiclesByName.add(vehicle);
                        loadedCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Error loading vehicle: " + e.getMessage());
                }
            }
            System.out.println("Loaded " + loadedCount + " vehicles from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e.getMessage());
                }
            }
        }
    }

    private int extractNumber(String str, int defaultValue) {
        try {
            String[] parts = str.split(":");
            if (parts.length == 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            // Use default value
        }
        return defaultValue;
    }

    private double extractDouble(String str, double defaultValue) {
        try {
            String[] parts = str.split(":");
            if (parts.length == 2) {
                return Double.parseDouble(parts[1]);
            }
        } catch (Exception e) {
            // Use default value
        }
        return defaultValue;
    }

    private boolean extractBoolean(String str, boolean defaultValue) {
        try {
            String[] parts = str.split(":");
            if (parts.length == 2) {
                return Boolean.parseBoolean(parts[1]);
            }
        } catch (Exception e) {
            // Use default value
        }
        return defaultValue;
    }

    // Generate Enhanced Report
    public String generateEnhancedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ENHANCED FLEET REPORT ===\n\n");

        if (fleet.isEmpty()) {
            sb.append("Fleet is empty. No vehicles to generate report for.\n");
            return sb.toString();
        }

        // Collection Statistics
        sb.append("COLLECTION STATISTICS:\n");
        sb.append("Total Vehicles: ").append(fleet.size()).append("\n");
        sb.append("Unique Models: ").append(uniqueModels.size()).append("\n");
        sb.append("Alphabetically Sorted Vehicles: ").append(vehiclesByName.size()).append("\n\n");

        // Vehicle Type Distribution
        Map<String, Long> typeCount = fleet.stream()
                .collect(Collectors.groupingBy(v -> v.getClass().getSimpleName(), Collectors.counting()));

        sb.append("VEHICLE DISTRIBUTION:\n");
        typeCount.forEach((type, count) -> sb.append("  ").append(type).append(": ").append(count).append("\n"));
        sb.append("\n");

        // Speed Analysis
        Vehicle fastest = Collections.max(fleet, speedComparator);
        Vehicle slowest = Collections.min(fleet, speedComparator);

        sb.append("SPEED ANALYSIS:\n");
        sb.append("  Fastest: ").append(fastest.getModel()).append(" (").append(fastest.getMaxSpeed()).append(" km/h)\n");
        sb.append("  Slowest: ").append(slowest.getModel()).append(" (").append(slowest.getMaxSpeed()).append(" km/h)\n\n");

        // Fuel Status
        sb.append("FUEL STATUS:\n");
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                FuelConsumable fc = (FuelConsumable) v;
                sb.append("  ").append(v.getId()).append(" (").append(v.getModel()).append("): ")
                        .append(String.format("%.1f", fc.getFuelLevel())).append(" L\n");
            }
        }
        sb.append("\n");

        // Passenger Information
        sb.append("PASSENGER INFORMATION:\n");
        for (Vehicle v : fleet) {
            if (v instanceof PassengerCarrier) {
                PassengerCarrier pc = (PassengerCarrier) v;
                sb.append("  ").append(v.getId()).append(" (").append(v.getModel()).append("): ")
                        .append(pc.getCurrentPassengers()).append("/").append(pc.getPassengerCapacity()).append(" passengers\n");
            }
        }
        sb.append("\n");

        // Cargo Information
        sb.append("CARGO INFORMATION:\n");
        for (Vehicle v : fleet) {
            if (v instanceof CargoCarrier) {
                CargoCarrier cc = (CargoCarrier) v;
                sb.append("  ").append(v.getId()).append(" (").append(v.getModel()).append("): ")
                        .append(String.format("%.1f", cc.getCurrentCargo())).append("/")
                        .append(String.format("%.1f", cc.getCargoCapacity())).append(" kg\n");
            }
        }
        sb.append("\n");

        // Unique Models
        sb.append("UNIQUE MODELS:\n");
        for (String model : uniqueModels) {
            sb.append("  - ").append(model).append("\n");
        }
        

        /// ============Sort it by fuel Efficenecy =============================
        if (!fleet.isEmpty()) {
    Vehicle mostEfficient = Collections.min(fleet, efficiencyComparator);
    Vehicle leastEfficient = Collections.max(fleet, efficiencyComparator);
    
    sb.append("FUEL EFFICIENCY ANALYSIS:\n");
    sb.append("  Most Efficient: ").append(mostEfficient.getModel())
      .append(" (").append(mostEfficient.calculateFuelEfficiency()).append(" km/L)\n");
    sb.append("  Least Efficient: ").append(leastEfficient.getModel())
      .append(" (").append(leastEfficient.calculateFuelEfficiency()).append(" km/L)\n\n");
}

        return sb.toString();
    }

    // Getters
    public List<Vehicle> getFleet() { return new ArrayList<>(fleet); }
    public int getFleetSize() { return fleet.size(); }
    public int getUniqueModelCount() { return uniqueModels.size(); }
    public boolean isFleetEmpty() { return fleet.isEmpty(); }
}