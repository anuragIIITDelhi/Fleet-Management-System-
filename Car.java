class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel = 0.0;
    private final int passengerCapacity = 5;
    private int currentPassengers = 0;
    private boolean maintenanceNeeded = false;

    public Car(String id, String model, double maxSpeed, double currentMileage, int numWheels)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
    }

    // Constructor with all data for loading from file
    public Car(String id, String model, double maxSpeed, double currentMileage, int numWheels,
               double fuelLevel, int currentPassengers) throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.fuelLevel = fuelLevel;
        this.currentPassengers = currentPassengers;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel to move.");
        fuelLevel -= requiredFuel;
        addMileage(distance);
        System.out.println("Car " + getId() + " driving on road... Distance: " + distance + " km");
    }

    @Override
    public double calculateFuelEfficiency() { return 15.0; }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive.");
        fuelLevel += amount;
        System.out.println("Car " + getId() + " refueled with " + amount + "L. Current fuel: " + fuelLevel + "L");
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) throw new InsufficientFuelException("Not enough fuel.");
        fuelLevel -= requiredFuel;
        return requiredFuel;
    }

    @Override
    public void boardPassengers(int count) throws OverloadException, InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (currentPassengers + count > passengerCapacity) throw new OverloadException("Passenger limit exceeded.");
        currentPassengers += count;
        System.out.println("Car " + getId() + " boarded " + count + " passengers. Total: " + currentPassengers + "/" + passengerCapacity);
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (count > currentPassengers) throw new InvalidOperationException("Not enough passengers to disembark.");
        currentPassengers -= count;
        System.out.println("Car " + getId() + " disembarked " + count + " passengers. Total: " + currentPassengers + "/" + passengerCapacity);
    }

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
        System.out.println("Car " + getId() + " maintenance performed. Vehicle is ready.");
    }

    // Getters for file saving
    public double getCurrentFuel() { return fuelLevel; }
    public int getCurrentPassengerCount() { return currentPassengers; }
}