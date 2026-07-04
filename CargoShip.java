class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private final double cargoCapacity = 50000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;
    private double fuelLevel = 0.0;

    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, hasSail);
    }

    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail,
                     double fuelLevel, double currentCargo) throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, hasSail);
        this.fuelLevel = fuelLevel;
        this.currentCargo = currentCargo;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");

        if (!hasSail()) {
            double requiredFuel = distance / calculateFuelEfficiency();
            if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel to sail the distance!");
            fuelLevel -= requiredFuel;
        }

        addMileage(distance);
        System.out.println("CargoShip " + getId() + " sailing with cargo... Distance: " + distance + " km");
    }

    @Override
    public double calculateFuelEfficiency() {
        return hasSail() ? 0.0 : 4.0;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()) throw new InvalidOperationException("This cargo ship uses a sail and cannot be refueled.");
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive.");
        fuelLevel += amount;
        System.out.println("CargoShip " + getId() + " refueled with " + amount + "L. Current fuel: " + fuelLevel + "L");
    }

    @Override
    public double getFuelLevel() {
        return hasSail() ? 0.0 : fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (hasSail()) return 0.0;
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel for this journey!");
        fuelLevel -= requiredFuel;
        return requiredFuel;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Cargo capacity exceeded!");
        currentCargo += weight;
        System.out.println("CargoShip " + getId() + " loaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (weight > currentCargo) throw new InvalidOperationException("Invalid cargo weight to unload.");
        currentCargo -= weight;
        System.out.println("CargoShip " + getId() + " unloaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
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
        System.out.println("Cargo ship " + getId() + " maintenance performed. Ship is ready.");
    }

    // Getters for file saving
    public double getCurrentFuel() { return fuelLevel; }
    public double getCurrentCargoWeight() { return currentCargo; }
}