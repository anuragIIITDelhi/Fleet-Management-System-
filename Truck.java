class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel = 0.0;
    private final double cargoCapacity = 5000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Truck(String id, String model, double maxSpeed, double currentMileage, int numWheels)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
    }

    // Constructor with all data for loading from file
    public Truck(String id, String model, double maxSpeed, double currentMileage, int numWheels,
                 double fuelLevel, double currentCargo) throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.fuelLevel = fuelLevel;
        this.currentCargo = currentCargo;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative!");
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel to move truck.");
        fuelLevel -= requiredFuel;
        addMileage(distance);
        System.out.println("Truck " + getId() + " hauling cargo... Distance: " + distance + " km");
    }

    @Override
    public double calculateFuelEfficiency() {
        return (currentCargo > cargoCapacity * 0.5) ? 8.0 * 0.9 : 8.0;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive for truck!");
        fuelLevel += amount;
        System.out.println("Truck " + getId() + " refueled with " + amount + "L. Current fuel: " + fuelLevel + "L");
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel in the truck!");
        fuelLevel -= requiredFuel;
        return requiredFuel;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Cargo capacity exceeded.");
        currentCargo += weight;
        System.out.println("Truck " + getId() + " loaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (weight > currentCargo) throw new InvalidOperationException("Not enough cargo to unload.");
        currentCargo -= weight;
        System.out.println("Truck " + getId() + " unloaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
    }

    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    @Override
    public double getCurrentCargo() { return currentCargo; }

    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }

    @Override
    public boolean needsMaintenance() { return getCurrentMileage() > 10000 || maintenanceNeeded; }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Truck " + getId() + " maintenance performed. Vehicle is ready.");
    }

    // Getters for file saving
    public double getCurrentFuel() { return fuelLevel; }
    public double getCurrentCargoWeight() { return currentCargo; }
}