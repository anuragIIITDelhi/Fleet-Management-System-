abstract class AirVehicle extends Vehicle {
    private final double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double currentMileage, double maxAltitude)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage);
        if (maxAltitude <= 0) throw new InvalidOperationException("Max altitude must be positive.");
        this.maxAltitude = maxAltitude;
    }

    public double getMaxAltitude() { return maxAltitude; }

    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double baseTime = distance / getMaxSpeed();
        return baseTime * 0.95;
    }
}
