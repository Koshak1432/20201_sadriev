package kosh.snake;

public class Engine {
    public Engine(Coordinates startPos, int width, int height) {
        field = new Field(width, height);
        snake = new Snake(startPos);
        field.setSnake(startPos);
        addWalls(field);
        addFood(field);
    }

    public void addWalls(Field field) {
        //todo
    }
    public void addFood(Field field) {
        int countOfFoodOnField = 5;
        for (int i = 0; i < countOfFoodOnField; ++i) {
            field.setRandomFood();
        }
    }

    //true if ok, false if died
    public boolean makeStep(Direction direction) {
        snake.setDirection(direction);
        Coordinates nextCoords = snake.getNextCoords(snake.getHeadCoords(), field.getWidth(), field.getHeight());
        if (field.isValidPosition(nextCoords)) {
            snake.growTo(nextCoords);
            field.setSnake(nextCoords);
            if (field.isFood(nextCoords)) {
                field.setRandomFood();
                ++score;
            } else {
                field.setEmpty(snake.loseTail());
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean snakeIsAlive() {
        return field.isValidPosition(snake.getHeadCoords());
    }

    public int getScore() {
        return score;
    }

    private final Field field;
    private final Snake snake;
    private int score = 0;
}
