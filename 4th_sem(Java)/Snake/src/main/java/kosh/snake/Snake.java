package kosh.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Snake {
    public Snake(ICoordinates headCoords) {
        this.headCoords = headCoords;
        snakeParts.add(headCoords);
        fillOppositeDirections();
    }

    private void fillOppositeDirections() {
        oppositeDirections.put(Direction.UP, Direction.DOWN);
        oppositeDirections.put(Direction.DOWN, Direction.UP);
        oppositeDirections.put(Direction.LEFT, Direction.RIGHT);
        oppositeDirections.put(Direction.RIGHT, Direction.LEFT);
    }

    public void growTo(ICoordinates coords) {
        snakeParts.add(coords);
        headCoords = coords;
    }

    public void setDirection(Direction newDirection) {
        if (newDirection != oppositeDirections.get(direction) && previousDirection != oppositeDirections.get(newDirection)) {
            previousDirection = this.direction;
            direction = newDirection;
        }
    }

    public ICoordinates getHeadCoords() {
        return headCoords;
    }

    public ICoordinates loseTail() {
        return snakeParts.poll();
    }

    public ICoordinates getNextCoords(ICoordinates currentCoords, int width, int height) {
        ICoordinates nextCoords = new Coordinates(currentCoords.x(), currentCoords.y());
        switch (direction) {
            case UP -> {
                if (currentCoords.y() - 1 < 0) {
                    nextCoords = new Coordinates(currentCoords.x(), height - 1);
                } else {
                    nextCoords = new Coordinates(currentCoords.x(), currentCoords.y() - 1);
                }
            }
            case DOWN -> {
                if (currentCoords.y() + 1 > height - 1) {
                    nextCoords = new Coordinates(currentCoords.x(), 0);
                } else {
                    nextCoords = new Coordinates(currentCoords.x(), currentCoords.y() + 1);
                }
            }
            case RIGHT -> {
                if (currentCoords.x() + 1 > width - 1) {
                    nextCoords = new Coordinates(0, currentCoords.y());
                } else {
                    nextCoords = new Coordinates(currentCoords.x() + 1, currentCoords.y());
                }
            }
            case LEFT -> {
                if (currentCoords.x() - 1 < 0) {
                    nextCoords =  new Coordinates(width - 1, currentCoords.y());
                } else {
                    nextCoords = new Coordinates(currentCoords.x() - 1, currentCoords.y());
                }
            }
        }
        return nextCoords;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public void updatePrevDirection() {
        previousDirection = direction;
    }

    private final Deque<ICoordinates> snakeParts = new ArrayDeque<>();
    private final Map<Direction, Direction> oppositeDirections = new HashMap<>();
    private ICoordinates headCoords;
    private int speed = 3;
    private Direction direction = Direction.RIGHT;
    private Direction previousDirection = Direction.RIGHT;
}