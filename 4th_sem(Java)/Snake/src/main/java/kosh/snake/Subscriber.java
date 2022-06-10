package kosh.snake;

import java.util.List;

public interface Subscriber {
    void handleEvent(Field field, List<Coordinates> coordsToRedraw, int score);
}
