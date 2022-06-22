package kosh.snake;

public interface IField {
    void setGrass(ICoordinates coords);
    void setFood(ICoordinates coords);
    ICoordinates setRandomFood();
    void setSnake(ICoordinates coords);
    void setWall(ICoordinates coords);
    TileState getCell(ICoordinates coords);
    boolean isValidPosition(ICoordinates coords);
    boolean isFood(ICoordinates coords);
    int getWidth();
    int getHeight();
}
