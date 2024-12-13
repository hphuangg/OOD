package org.example.parkinglot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotSystem {

    ParkingLot parkingLot; // singleton

    public ParkingLotSystem () {
        parkingLot = new ParkingLot();
    }

    public void addLevel(ParkingLevel level) {
        parkingLot.addLevel(level);
    }

    public boolean parkVehicle(Vehicle vehicle) {
        return parkingLot.parkVehicle(vehicle);
    }

    public static void main(String[] args) {

        ParkingLotSystem system = new ParkingLotSystem();

        ParkingLevel level1 = new ParkingLevel("Lv1");
        level1.addParkingSpots(VehicleType.MOTORCYCLE, 1);
        level1.addParkingSpots(VehicleType.CAR, 3);
        level1.addParkingSpots(VehicleType.TRUNK, 2);
        system.addLevel(level1);


        system.parkingLot.displayParkingSpots();
        System.out.println("------------------------------------");
        Car car1 = new Car("Car1");
        system.parkVehicle(car1);
        System.out.println("------------------------------------");

        system.parkingLot.displayParkingSpots();
        System.out.println("------------------------------------");
        Motorcycle motorcycle = new Motorcycle("Motorcycle1");
        system.parkVehicle(motorcycle);
        System.out.println("------------------------------------");

        system.parkingLot.displayParkingSpots();
        System.out.println("------------------------------------");
        Motorcycle motorcycle2 = new Motorcycle("Motorcycle2");
        system.parkVehicle(motorcycle2);
        System.out.println("------------------------------------");

        system.parkingLot.displayParkingSpots();
    }

}


@AllArgsConstructor
class ParkingLot {

    private final List<ParkingLevel> levels;

    private final List<Gate> entryGates;

    private final List<Gate> exitGates;

    public ParkingLot() {
        this.levels = new ArrayList<>();
        this.entryGates = new ArrayList<>();
        this.exitGates = new ArrayList<>();
    }

    public void addLevel(ParkingLevel parkingLevel) {
        levels.add(parkingLevel);
    }

    public void addEntryGate(Gate gate) {
        entryGates.add(gate);
    }

    public void addExitGate(Gate gate) {
        exitGates.add(gate);
    }

    public boolean canPark(Vehicle vehicle) {
       return getAvailableSpotsCount(vehicle) > 0;
    }

    public boolean parkVehicle(Vehicle vehicle) {
        for (ParkingLevel parkingLevel : levels) {
            ParkingSpot availableSpot = parkingLevel.findAvailableSpot(vehicle);
            if (availableSpot != null) {
                availableSpot.parkVehicle(vehicle);
                System.out.println(vehicle.getLicensePlate() + " is parked at spot " + availableSpot.getSpotId());
                return true;
            }
        }
        System.out.println("no parking spot is available for " + vehicle.getLicensePlate());
        return false;
    }

    public int getAvailableSpotsCount(Vehicle vehicle) {
        return findAvailableSpots(vehicle).size();
    }

    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingLevel level : levels) {
            availableSpots.addAll(level.findAvailableSpots(vehicle));
        }
        return availableSpots;
    }

    public void displayParkingSpots() {
        for (ParkingLevel level : levels) {
            System.out.println("current level: " + level.getId());
            for (ParkingSpot parkingSpot : level.getParkingSpots()) {
                System.out.printf("spot: %s, status: %s%n",
                        parkingSpot.getSpotId(),
                        parkingSpot.isAvailable() ? "available" : "occupied by" + parkingSpot.getParkedVehicle().getLicensePlate());
            }
        }
    }

}

@Value
class ParkingLevel {

    final String id;

    final List<ParkingSpot> parkingSpots;

    public ParkingLevel(String id) {
        this.id = id;
        this.parkingSpots = new ArrayList<>();
    }

    public void addParkingSpots(VehicleType vehicleType, int count) {
        for (int i = 0; i < count; i++) {
            String spotId = id + "_" + vehicleType.name().charAt(0) + (i + 1);
            parkingSpots.add(new ParkingSpot(id, spotId, vehicleType));
        }
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot parkingSpot : parkingSpots) {
            if (parkingSpot.canPark(vehicle)) {
                return parkingSpot;
            }
        }
        return null;
    }

    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        return parkingSpots.stream()
                .filter(parkingSpot -> parkingSpot.canPark(vehicle))
                .toList();
    }
}

@Getter
class ParkingSpot {
    String levelId;
    String spotId;
    VehicleType vehicleType;
    Vehicle parkedVehicle;

    public ParkingSpot(String levelId, String spotId, VehicleType type) {
        this.levelId = levelId;
        this.spotId = spotId;
        this.vehicleType = type;
    }

    public boolean isAvailable() {
        return this.parkedVehicle == null;
    }

    public boolean canPark(Vehicle vehicle) {
        return isAvailable() && vehicle.type.equals(vehicleType);
    }

    public void parkVehicle(Vehicle vehicle) {
        if (!canPark(vehicle)) {
            return;
        }
        parkedVehicle = vehicle;
    }

    public void removeParkedVehicle(Vehicle vehicle) {
        if (parkedVehicle == null || !vehicle.licensePlate.equals(parkedVehicle.licensePlate)) {
            return;
        }

        parkedVehicle = null;
    }
}

enum VehicleType {
    MOTORCYCLE, CAR, TRUNK
}

@AllArgsConstructor
@Getter
abstract class Vehicle {
    String licensePlate;
    VehicleType type;
}

class Motorcycle extends Vehicle {

    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
}

class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
}

class Trunk extends Vehicle {

    public Trunk(String licensePlate) {
        super(licensePlate, VehicleType.TRUNK);
    }
}

@AllArgsConstructor
abstract class Gate {
    int gateId;
    GateType type;

    abstract public boolean processVehicle(Vehicle vehicle);
}

class EntryGate extends Gate {

    public EntryGate(int gateId) {
        super(gateId, GateType.ENTRY);
    }

    @Override
    public boolean processVehicle(Vehicle vehicle) {
        return false;
    }

}

class ExitGate extends Gate {

    public ExitGate(int gateId) {
        super(gateId, GateType.EXIT);
    }

    @Override
    public boolean processVehicle(Vehicle vehicle) {
        return false;
    }
}

enum GateType {
    ENTRY, EXIT
}