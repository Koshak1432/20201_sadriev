package kosh.snake;

import java.util.ArrayList;
import java.util.Random;

public class Field implements IField {
    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new TileState[height][width];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                setGrass(new Coordinates(x, y));
            }
        }
    }

    @Override
    public void setGrass(ICoordinates coords) {
        field[coords.y()][coords.x()] = TileState.GRASS;
        emptyCells.add(coords);
    }
    @Override
    public void setFood(ICoordinates coords) {
        field[coords.y()][coords.x()] = TileState.FOOD;
        emptyCells.remove(coords);
    }
    @Override
    public ICoordinates setRandomFood() {
        ICoordinates foodCoords = emptyCells.get(random.nextInt(emptyCells.size()));
        setFood(foodCoords);
        return foodCoords;
    }
    @Override
    public void setSnake(ICoordinates coords) {
        field[coords.y()][coords.x()] = TileState.SNAKE;
        emptyCells.remove(coords);
    }

    @Override
    public void setWall(ICoordinates coords) {
        field[coords.y()][coords.x()] = TileState.WALL;
        emptyCells.remove(coords);
    }
    @Override
    public TileState getCell(ICoordinates coords) {
        return field[coords.y()][coords.x()];
    }

    @Override
    public boolean isValidPosition(ICoordinates coords) {
        TileState state = getCell(coords);
        return state == TileState.GRASS ||  state == TileState.FOOD;
    }

    @Override
    public boolean isFood(ICoordinates coords) {
        return getCell(coords) == TileState.FOOD;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private final TileState[][] field;
    private final ArrayList<ICoordinates> emptyCells = new ArrayList<>();
    private final int width;
    private final int height;
    private final Random random = new Random();
}