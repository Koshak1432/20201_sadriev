package kosh.snake;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Engine implements Publisher {

    //true if ok, false if died
    public boolean makeStep() {
        boolean alive = true;
        Coordinates nextCoords = snake.getNextCoords(snake.getHeadCoords(), field.getWidth(), field.getHeight());
        if (field.isFood(nextCoords)) {
            coordsToRedraw.add(field.setRandomFood());
            ++score;
            snake.setSpeed(snake.getSpeed() + 1);
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

    public int getScore() {
        return score;
    }

    private boolean readFieldParams(String levelFile) {
        try (InputStream in = getClass().getResourceAsStream(levelFile)) {
            assert in != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String string = reader.readLine();
                String[] params = string.split(",");
                for (String param : params) {
                    int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                    if (param.contains("x=")) {
                        fieldWidth = value;
                    } else if (param.contains("y=")) {
                        fieldHeight = value;
                    }
                    else {
                        System.err.println("Can't read params for field");
                        return false;
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private char[][] convertLevelInfoToArray(String levelFile) {
        char[][] levelToParse = new char[fieldHeight][fieldWidth];
        String string;
        int countLine = 0;
        try (InputStream in = getClass().getResourceAsStream(levelFile)) {
            assert in != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                reader.readLine(); //first line is for field params
                while ((string = reader.readLine()) != null) {
                    if (string.length() != fieldWidth || countLine == fieldHeight) {
                        System.err.println("Invalid file to load");
                        return null;
                    }
                    levelToParse[countLine++] = string.toCharArray();
                }
                if (countLine != fieldHeight) {
                    System.err.println("Read lines don't equal y field param");
                    return null;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return levelToParse;
    }

    private boolean parseInfoLevel(char[][] levelInfo) {
        if (levelInfo == null) {
            return false;
        }
        for (int y = 0; y < fieldHeight; ++y) {
            for (int x = 0; x < fieldWidth; ++x) {
                Coordinates coords = new Coordinates(x, y);
                switch (levelInfo[y][x]) {
                    case 'w' -> field.setWall(coords);
                    case 's' -> {
                        if (snake == null) {
                            snake = new Snake(coords);
                            field.setSnake(coords);
                        } else {
                            System.err.println("Invalid fileInfo: several snake coordinates, only 1 coordinate is for snake head");
                            return false;
                        }
                    }
                    case 'g' -> field.setEmpty(coords);
                    case 'f' -> field.setFood(coords);
                    default -> {
                        System.err.println("Unknown symbol");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean loadField(String levelFile) {
        if (!readFieldParams(levelFile)) {
            System.err.println("Can't read field's params");
            return false;
        }
        field = new Field(fieldWidth, fieldHeight);

        char[][] info = convertLevelInfoToArray(levelFile);
        if (info == null) {
            System.err.println("Can't read level info");
            return false;
        }

        if (!parseInfoLevel(info)) {
            System.err.println("Can't parse the loaded info");
            return false;
        }
        return true;
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
            sub.handleEvent(field, coordsToRedraw, score);
        }
    }

    private Field field;
    private Snake snake;
    private int score = 0;
    private int fieldWidth;
    private int fieldHeight;
    private final List<Subscriber> subscribers = new ArrayList<>();
    private final List<Coordinates> coordsToRedraw = new ArrayList<>();

}