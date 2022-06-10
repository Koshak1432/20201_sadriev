package kosh.snake;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Snake {
    public Snake(Coordinates headCoords) {
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

    public void growTo(Coordinates coords) {
        snakeParts.add(coords);
        headCoords = coords;
    }

    public void setDirection(Direction direction) {
        if (oppositeDirections.get(direction) != this.direction) {
            this.direction = direction;
        }
    }

    public Coordinates getHeadCoords() {
        return headCoords;
    }

    public Coordinates loseTail() {
        return snakeParts.poll();
    }

    public Coordinates getNextCoords(Coordinates currentCoords, int width, int height) {
        Coordinates nextCoords = new Coordinates(currentCoords.x(), currentCoords.y());
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

    private final ArrayDeque<Coordinates> snakeParts = new ArrayDeque<>();
    private final Map<Direction, Direction> oppositeDirections = new HashMap<>();
    private Coordinates headCoords;
    private int speed = 3;
    private Direction direction = Direction.RIGHT;
}