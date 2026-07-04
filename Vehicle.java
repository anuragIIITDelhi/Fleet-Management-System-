abstract class Vehicle implements Comparable<Vehicle> {
    private final String id;
    private String model;
    private final double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed, double currentMileage) throws InvalidOperationException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidOperationException("ID cannot be empty.");
        }
        if (maxSpeed <= 0) {
            throw new InvalidOperationException("Max speed must be positive.");
        }
        if (currentMileage < 0) {
            throw new InvalidOperationException("Mileage cannot be negative.");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = currentMileage;
    }

    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(this.calculateFuelEfficiency(), other.calculateFuelEfficiency());
    }

    public abstract void move(double distance) throws InvalidOperationException, InsufficientFuelException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance) throws InvalidOperationException;

    public void displayInfo() {
        System.out.println("ID: " + id + ", Model: " + model +
                ", Max Speed: " + maxSpeed + " km/h, Mileage: " + currentMileage + " km");
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getCurrentMileage() { return currentMileage; }

    protected void addMileage(double distance) {
        this.currentMileage += distance;
    }
}