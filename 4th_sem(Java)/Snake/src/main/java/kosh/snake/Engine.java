package kosh.snake;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ArrayChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Engine implements Publisher{
    public Engine(Coordinates startPos) {
        field = new Field();
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
    public boolean makeStep() {
        boolean alive = true;
        Coordinates nextCoords = snake.getNextCoords(snake.getHeadCoords(), field.getWidth(), field.getHeight());
        if (field.isFood(nextCoords)) {
            coordsToRedraw.add(field.setRandomFood());
            ++score;
            System.out.println("SCORE: " + score);
        } else {
            Coordinates tail = snake.loseTail();
            coordsToRedraw.add(tail);
            field.setEmpty(tail);
        }
        if (field.isValidPosition(nextCoords)) {
            snake.growTo(nextCoords);
            field.setSnake(nextCoords);
            coordsToRedraw.add(nextCoords);
        } else {
            alive = false;
        }
        notifySubscribers();
        return alive;
    }

    public boolean snakeIsAlive() {
        return field.isValidPosition(snake.getHeadCoords());
    }

    public int getScore() {
        return score;
    }

    public void loadField(String level) {

    }

    public Field getField() {
        return field;
    }

    public Snake getSnake() {
        return snake;
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void notifySubscribers() {
        for (Subscriber sub : subscribers) {
            sub.handleEvent(field, coordsToRedraw);
        }
    }
    private final Field field;
    private final Snake snake;
    private int score = 0;

    private final List<Subscriber> subscribers = new ArrayList<>();
    private final List<Coordinates> coordsToRedraw = new ArrayList<>();

}