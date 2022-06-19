package kosh.snake;

import java.util.ArrayList;
import java.util.Random;

public class Field {
    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new TileState[height][width];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                setEmpty(new Coordinates(x, y));
            }
        }
    }

    public void setEmpty(Coordinates coords) {
        field[coords.y()][coords.x()] = TileState.EMPTY;
        emptyCells.add(coords);
    }

    public void setFood(Coordinates coords) {
        field[coords.y()][coords.x()] = TileState.FOOD;
        emptyCells.remove(coords);
    }

    public Coordinates setRandomFood() {
        Coordinates foodCoords = emptyCells.get(random.nextInt(emptyCells.size()));
        setFood(foodCoords);
        return foodCoords;
    }

    public void setSnake(Coordinates coords) {
        field[coords.y()][coords.x()] = TileState.SNAKE;
        emptyCells.remove(coords);
    }

    public void setWall(Coordinates coords) {
        field[coords.y()][coords.x()] = TileState.WALL;
        emptyCells.remove(coords);
    }

    public TileState getCell(Coordinates coords) {
        return field[coords.y()][coords.x()];
    }

    public boolean isValidPosition(Coordinates coords) {
        TileState state = getCell(coords);
        return state == TileState.EMPTY ||  state == TileState.FOOD;
    }

    public boolean isFood(Coordinates coords) {
        return getCell(coords) == TileState.FOOD;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private final TileState[][] field;
    private final ArrayList<Coordinates> emptyCells = new ArrayList<>();
    private final int width;
    private final int height;
    private final Random random = new Random();
}