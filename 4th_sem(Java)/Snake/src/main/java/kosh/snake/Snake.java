package kosh.snake;

import java.util.ArrayDeque;

public class Snake {
    public Snake(Coordinates headCoords) {
        this.headCoords = headCoords;
        snakeParts.add(headCoords);
    }

    public void growTo(Coordinates coords) {
        snakeParts.add(coords);
        headCoords = coords;
    }

    public void setDirection(Direction direction) {
        previousDirection = this.direction;
        this.direction = direction;
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
                if (previousDirection != Direction.DOWN) {
                    if (currentCoords.y() - 1 < 0) {
                        nextCoords = new Coordinates(currentCoords.x(), height - 1);
                    } else {
                        nextCoords = new Coordinates(currentCoords.x(), currentCoords.y() - 1);
                    }
                }
            }
            case DOWN -> {
                if (previousDirection != Direction.UP) {
                    if (currentCoords.y() + 1 > height - 1) {
                        nextCoords = new Coordinates(currentCoords.x(), 0);
                    } else {
                        nextCoords = new Coordinates(currentCoords.x(), currentCoords.y() + 1);
                    }
                }
            }
            case RIGHT -> {
                if (previousDirection != Direction.LEFT) {
                    if (currentCoords.x() + 1 > width - 1) {
                        nextCoords = new Coordinates(0, currentCoords.y());
                    } else {
                        nextCoords = new Coordinates(currentCoords.x() + 1, currentCoords.y());
                    }
                }
            }
            case LEFT -> {
                if (previousDirection != Direction.RIGHT) {
                    if (currentCoords.x() - 1 < 0) {
                        nextCoords =  new Coordinates(width - 1, currentCoords.y());
                    } else {
                        nextCoords = new Coordinates(currentCoords.x() - 1, currentCoords.y());
                    }
                }
            }
        }
        return nextCoords;
    }

    private final ArrayDeque<Coordinates> snakeParts = new ArrayDeque<>();
    private Coordinates headCoords;
    private Direction direction = Direction.RIGHT;
    private Direction previousDirection = Direction.DOWN;
}
