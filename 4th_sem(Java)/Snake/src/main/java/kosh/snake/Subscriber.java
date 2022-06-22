package kosh.snake;

import java.util.List;

public interface Subscriber {
    void handleEvent(IField field, List<ICoordinates> coordsToRedraw, int score);
}
