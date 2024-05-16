import java.util.*;

// Main class to demonstrate the usage of the parking lot
public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(2, 2);

        // Initializing parking lot
        init(parkingLot);

        // Adding vehicles
        Vehicle car1 = new Vehicle("ABC123", "Red");
        Vehicle car2 = new Vehicle("XYZ456", "Blue");

        addVehicle(parkingLot, VehicleType.CAR, car1);
        addVehicle(parkingLot, VehicleType.CAR, car2);

        // Removing vehicle
        removeVehicle(parkingLot, car1);

        // Checking availability
        checkAvailability(parkingLot, 1, VehicleType.CAR);

        // Testing full capacity
        Vehicle car3 = new Vehicle("DEF789", "Green");
        addVehicle(parkingLot, VehicleType.CAR, car3);
    }

    public static void init(ParkingLot parkingLot) {
        System.out.println("Parking lot initialized with " + parkingLot.getNumberOfFloors() + " floors and " +
                parkingLot.getCapacityPerType() + " spaces per type.");
    }

    public static void addVehicle(ParkingLot parkingLot, VehicleType type, Vehicle vehicle) {
        if (parkingLot.isParkingAvailable(type)) {
            VehicleSpace space = parkingLot.parkVehicle(type, vehicle);
            System.out.println("Vehicle parked at floor " + space.getFloorNumber() +
                    ", space " + space.getSpaceNumber());
        } else {
            System.out.println("Parking is full for type " + type);
        }
    }

    public static void removeVehicle(ParkingLot parkingLot, Vehicle vehicle) {
        parkingLot.removeVehicle(vehicle);
        System.out.println("Vehicle removed from parking lot");
    }

    public static void checkAvailability(ParkingLot parkingLot, int floorNumber, VehicleType type) {
        if (parkingLot.isParkingAvailable(type)) {
            System.out.println("Parking available for type " + type + " on floor " + floorNumber);
        } else {
            System.out.println("No parking available for type " + type + " on floor " + floorNumber);
        }
    }
}

// Vehicle class representing a generic vehicle
class Vehicle {
    private String registrationNumber;
    private String color;

    public Vehicle(String registrationNumber, String color) {
        this.registrationNumber = registrationNumber;
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getColor() {
        return color;
    }
}

// VehicleType enum representing different types of vehicles
enum VehicleType {
    BIKE,
    CAR,
    BUS
}

// VehicleSpace class representing a parking space
class VehicleSpace {
    private int spaceNumber;
    private int floorNumber;
    private boolean available;
    private VehicleType type;
    private Vehicle parkedVehicle;

    public VehicleSpace(int floorNumber, int spaceNumber, VehicleType type) {
        this.floorNumber = floorNumber;
        this.spaceNumber = spaceNumber;
        this.available = true;
        this.type = type;
        this.parkedVehicle = null;
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public VehicleType getType() {
        return type;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public void parkVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.available = false;
    }

    public void removeVehicle() {
        this.parkedVehicle = null;
        this.available = true;
    }
}

// Floor class representing a parking floor
class Floor {
    private int floorNumber;
    private List<VehicleSpace> vehicleSpaces;

    public Floor(int floorNumber, int capacityPerType) {
        this.floorNumber = floorNumber;
        this.vehicleSpaces = new ArrayList<>();
        for (int i = 1; i <= capacityPerType; i++) {
            vehicleSpaces.add(new VehicleSpace(floorNumber, i, VehicleType.BIKE));
            vehicleSpaces.add(new VehicleSpace(floorNumber, i, VehicleType.CAR));
            vehicleSpaces.add(new VehicleSpace(floorNumber, i, VehicleType.BUS));
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<VehicleSpace> getVehicleSpaces() {
        return vehicleSpaces;
    }

    public boolean isSpaceAvailable(VehicleType type) {
        for (VehicleSpace space : vehicleSpaces) {
            if (space.isAvailable() && space.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public VehicleSpace parkVehicle(VehicleType type, Vehicle vehicle) {
        for (VehicleSpace space : vehicleSpaces) {
            if (space.isAvailable() && space.getType() == type) {
                space.parkVehicle(vehicle);
                return space;
            }
        }
        return null; // No available space
    }

    public void removeVehicle(Vehicle vehicle) {
        for (VehicleSpace space : vehicleSpaces) {
            if (!space.isAvailable() && space.getParkedVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber())) {
                space.removeVehicle();
                return;
            }
        }
    }
}

// ParkingLot class representing the parking lot
class ParkingLot {
    private int numberOfFloors;
    private int capacityPerType;
    private List<Floor> floors;

    public ParkingLot(int numberOfFloors, int capacityPerType) {
        this.numberOfFloors = numberOfFloors;
        this.capacityPerType = capacityPerType;
        this.floors = new ArrayList<>();
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new Floor(i, capacityPerType));
        }
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public int getCapacityPerType() {
        return capacityPerType;
    }

    public boolean isParkingAvailable(VehicleType type) {
        for (Floor floor : floors) {
            if (floor.isSpaceAvailable(type)) {
                return true;
            }
        }
        return false;
    }

    public VehicleSpace parkVehicle(VehicleType type, Vehicle vehicle) {
        for (Floor floor : floors) {
            if (floor.isSpaceAvailable(type)) {
                return floor.parkVehicle(type, vehicle);
            }
        }
        return null; // No available space
    }

    public void removeVehicle(Vehicle vehicle) {
        for (Floor floor : floors) {
            floor.removeVehicle(vehicle);
        }
    }
}

// CostStrategy interface representing the cost calculation strategy
interface CostStrategy {
    double calculateCost(VehicleType type, int durationInHours);
}

// FlatCostStrategy class implementing a flat cost structure
class FlatCostStrategy implements CostStrategy {
    private Map<VehicleType, Double> hourlyRates;

    public FlatCostStrategy() {
        hourlyRates = new HashMap<>();
        hourlyRates.put(VehicleType.BIKE, 10.0);
        hourlyRates.put(VehicleType.CAR, 20.0);
        hourlyRates.put(VehicleType.BUS, 30.0);
    }

    @Override
    public double calculateCost(VehicleType type, int durationInHours) {
        return hourlyRates.get(type) * durationInHours;
    }
}


