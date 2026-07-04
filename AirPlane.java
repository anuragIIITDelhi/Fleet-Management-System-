class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel = 0.0;
    private final int passengerCapacity = 200;
    private int currentPassengers = 0;
    private final double cargoCapacity = 10000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Airplane(String id, String model, double maxSpeed, double currentMileage, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, maxAltitude);
    }

    // Constructor with all data for loading from file
    public Airplane(String id, String model, double maxSpeed, double currentMileage, double maxAltitude,
                    double fuelLevel, int currentPassengers, double currentCargo) throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, maxAltitude);
        this.fuelLevel = fuelLevel;
        this.currentPassengers = currentPassengers;
        this.currentCargo = currentCargo;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative!");
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel to fly the distance!");
        fuelLevel -= requiredFuel;
        addMileage(distance);
        System.out.println("Airplane " + getId() + " flying at " + getMaxAltitude() + " meters... Distance: " + distance + " km");
    }

    @Override
    public double calculateFuelEfficiency() { return 5.0; }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive!");
        fuelLevel += amount;
        System.out.println("Airplane " + getId() + " refueled with " + amount + "L. Current fuel: " + fuelLevel + "L");
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel in the airplane!");
        fuelLevel -= requiredFuel;
        return requiredFuel;
    }

    @Override
    public void boardPassengers(int number) throws OverloadException, InvalidOperationException {
        if (number <= 0) throw new InvalidOperationException("Passenger number must be positive!");
        if (currentPassengers + number > passengerCapacity) throw new OverloadException("Passenger capacity exceeded!");
        currentPassengers += number;
        System.out.println("Airplane " + getId() + " boarded " + number + " passengers. Total: " + currentPassengers + "/" + passengerCapacity);
    }

    @Override
    public void disembarkPassengers(int number) throws InvalidOperationException {
        if (number <= 0) throw new InvalidOperationException("Passenger number must be positive!");
        if (number > currentPassengers) throw new InvalidOperationException("Not enough passengers to disembark!");
        currentPassengers -= number;
        System.out.println("Airplane " + getId() + " disembarked " + number + " passengers. Total: " + currentPassengers + "/" + passengerCapacity);
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive!");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Cargo capacity exceeded!");
        currentCargo += weight;
        System.out.println("Airplane " + getId() + " loaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive!");
        if (weight > currentCargo) throw new InvalidOperationException("Not enough cargo to unload!");
        currentCargo -= weight;
        System.out.println("Airplane " + getId() + " unloaded " + weight + "kg cargo. Total: " + currentCargo + "/" + cargoCapacity + "kg");
    }

    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    @Override
    public double getCurrentCargo() { return currentCargo; }

    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    @Override
    public int getCurrentPassengers() { return currentPassengers; }

    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }

    @Override
    public boolean needsMaintenance() { return getCurrentMileage() > 10000 || maintenanceNeeded; }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Airplane " + getId() + " maintenance completed.");
    }

    // Getters for file saving
    public double getCurrentFuel() { return fuelLevel; }
    public int getCurrentPassengerCount() { return currentPassengers; }
    public double getCurrentCargoWeight() { return currentCargo; }
}