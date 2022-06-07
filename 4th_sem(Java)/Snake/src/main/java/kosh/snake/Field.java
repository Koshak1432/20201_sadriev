package kosh.snake;

import java.util.ArrayList;
import java.util.Random;

public class Field {
    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new CellState[height][width];
        emptyCells = new ArrayList<>();
        wallCells = new ArrayList<>();
        random = new Random();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                setEmpty(new Coordinates(x, y));
            }
        }
    }

    public void setEmpty(Coordinates coords) {
        field[coords.y()][coords.x()] = CellState.EMPTY;
        emptyCells.add(coords);
    }

    public void setFood(Coordinates coords) {
        field[coords.y()][coords.x()] = CellState.FOOD;
        emptyCells.remove(coords);
    }

    public void setRandomFood() {
        setFood(emptyCells.get(random.nextInt(emptyCells.size())));
    }

    public void setSnake(Coordinates coords) {
        field[coords.y()][coords.x()] = CellState.SNAKE;
        emptyCells.remove(coords);
    }

    public void setWall(Coordinates coords) {
        field[coords.y()][coords.x()] = CellState.WALL;
        emptyCells.remove(coords);
        wallCells.add(coords);
    }

    public CellState getCell(Coordinates coords) {
        return field[coords.y()][coords.x()];
    }

    public boolean isValidPosition(Coordinates coords) {
        CellState state = getCell(coords);
        return state == CellState.EMPTY ||  state == CellState.FOOD;
    }

    public boolean isFood(Coordinates coords) {
        return getCell(coords) == CellState.FOOD;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private final CellState[][] field;
    private ArrayList<Coordinates> emptyCells;
    private ArrayList<Coordinates> wallCells;
    private final int width;
    private final int height;
    private final Random random;
}
