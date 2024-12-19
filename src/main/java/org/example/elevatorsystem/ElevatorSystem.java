package org.example.elevatorsystem;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class ElevatorSystem {
    public static void main(String[] args) {
        ElevatorDispatcher elevatorDispatcher = new ElevatorDispatcher(2);

        ExternalRequest upRequest = new ExternalRequest(1, 5, Direction.UP);
        ExternalRequest downRequest = new ExternalRequest(2,2, Direction.DOWN);

        elevatorDispatcher.handleRequest(upRequest);
        elevatorDispatcher.handleRequest(downRequest);
    }
}

abstract class Button {
    ButtonState buttonState;
    abstract void press();
}

enum ButtonState {
    UNPRESSED, PRESSED
}

class FloorButton extends Button {
    int floor;

    @Override
    void press() {
        // send request to elevator
    }
}

class OpenButton extends Button {

    @Override
    void press() {
        // send request to elevator
    }
}

class CloseButton extends Button {

    @Override
    void press() {
        // send request to elevator
    }
}

class UpButton extends Button {

    @Override
    void press() {

    }
}

class DownButton extends Button {

    @Override
    void press() {

    }
}

class InsidePanel {
    List<FloorButton> floorButtons;
    OpenButton openButton;
    CloseButton closeButton;

    public InsidePanel(int numberOfFloors) {
        this.floorButtons = new ArrayList<>();
        for (int i = 0; i < numberOfFloors; i++) {
            this.floorButtons.add(new FloorButton());
        }
        openButton = new OpenButton();
        closeButton = new CloseButton();
    }
}

class OutsidePanel {
    UpButton upButton;
    DownButton downButton;
}

@Data
class Elevator {
    String id;
    int currentFloor;
    Direction direction;
    InsidePanel insidePanel;
    PriorityQueue<Request> requests;

    public Elevator(String id, int numberOfFloors) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.insidePanel = new InsidePanel(numberOfFloors);
        requests = new PriorityQueue<>((r1, r2) ->
                        Integer.compare(Math.abs(currentFloor - r1.getFloor()), Math.abs(currentFloor - r2.getFloor())));
    }

    public void addRequest(Request request) {
        requests.offer(request);
    }

    public void move() {
        while (!requests.isEmpty()) {
            Request request = requests.poll();
            System.out.printf("Move elevator %s for request %d. %n", id, request.getId());
            moveToFloor(request.getFloor());

        }
        direction = Direction.IDLE;
    }

    private void moveToFloor(int floor) {
        int diffFloor = currentFloor - floor;
        direction = diffFloor > 0 ? Direction.DOWN : Direction.UP;
        System.out.printf(" - moving %s to %d floor from %d floor. %n", direction, floor, currentFloor);
        while (currentFloor != floor) {
            // to simulate moving state.
            if (direction.equals(Direction.UP)) {
                currentFloor++;
            } else {
                currentFloor--;
            }
        }
    }
}

enum Direction {
    UP, DOWN, IDLE
}


@Data
abstract class Request {

    int id;

    int floor;

    Direction direction;

    public Request(int id, int floor, Direction direction) {
        this.id = id;
        this.floor = floor;
        this.direction = direction;
    }
}

class InternalRequest extends Request {
    public InternalRequest (int id, int floor, Direction direction) {
        super(id, floor, direction);
    }

}

class ExternalRequest extends Request {
    public ExternalRequest (int id, int floor, Direction direction) {
        super(id, floor, direction);
    }
}


class ElevatorDispatcher {

    List<Elevator> elevators;

    public ElevatorDispatcher(int numberOfElevators) {
        elevators = new ArrayList<>();
        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(String.valueOf(i), 10));
        }
    }

    public void handleRequest(ExternalRequest request) {
        if (elevators.isEmpty()) {
            System.out.printf("there's no elevator available for request %d. %n", request.getId());
            return;
        }
        Optional<Elevator> optimalElevator = findOptimalElevator(request);
        if (optimalElevator.isPresent()) {
            Elevator elevator = optimalElevator.get();
            elevator.addRequest(request);
            elevator.move();
        } else {
            System.out.println("all elevators are busy, please try later.");
        }
    }

    private Optional<Elevator> findOptimalElevator(ExternalRequest request) {
        int minCost = Integer.MAX_VALUE;
        Optional<Elevator> optionalElevator = Optional.empty();
        for (Elevator elevator : elevators) {
            int cost = calculateCost(elevator, request);
            if (cost < minCost) {
                optionalElevator = Optional.of(elevator);
                minCost = cost;
            }
        }
        return optionalElevator;
    }

    /**
     * Implement cost calculation logic here (e.g., based on distance and direction)
     * - this logic can ask interviewer?
     * 1. elevator is idle (normal) +5
     * 2. elevator is moving toward requested floor, and same direction (best)
     * 3. elevator is moving toward requested floor and different direction (ok)  +10
     * 4. elevator is moving away requested floor. (worst) penalty
     *
     * */
    private int calculateCost(Elevator elevator, ExternalRequest request) {
        int cost = Math.abs(elevator.getCurrentFloor() - request.getFloor());
        if (elevator.getDirection() != Direction.IDLE && elevator.getDirection() != request.getDirection()) {
            cost += 10;
        }
        return cost;
    }
}

/** practice singleton
class ElevatorDispatcher {

    private static class SingletonHelper {
        private static final ElevatorDispatcher INSTANCE = new ElevatorDispatcher();
    }

    private ElevatorDispatcher() {};

    public static ElevatorDispatcher getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
*/