package kosh.snake;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Engine implements Publisher {
    /*
    * Makes game step
    * @return true if snake is alive, else false
    * */
    public boolean makeStep() {
        boolean alive = true;
        ICoordinates nextCoords = snake.getNextCoords(snake.getHeadCoords(), field.getWidth(), field.getHeight());
        if (field.isFood(nextCoords)) {
            coordsToRedraw.add(field.setRandomFood());
            ++score;
            snake.setSpeed(snake.getSpeed() + 1);
        } else {
            ICoordinates tail = snake.loseTail();
            coordsToRedraw.add(tail);
            field.setGrass(tail);
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

    /*
    * Reads field params: x, y
    * @param levelFile the name of level file to load
    * @return true if successfully read, else false
    * */
    private boolean readFieldParams(String levelFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.ABS_PATH_TO_RESOURCES + levelFile))) {
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
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
    * Reads level information from file into 2D char array
    * @param levelFile the name of level file to load
    * @return levelToParse 2D char array
    * */
    private char[][] convertLevelInfoToArray(String levelFile) {
        char[][] levelToParse = new char[fieldHeight][fieldWidth];
        String string;
        int countLine = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.ABS_PATH_TO_RESOURCES + levelFile))) {
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
        catch (IOException e) {
            e.printStackTrace();
        }
        return levelToParse;
    }

    /*
    * Parses 2D char array representing level
    * @param levelInfo 2D char array to parse
    * @return true if successfully parsed, else false
    * */
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
                    case 'g' -> field.setGrass(coords);
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

    /*
    * Loads game level
    * @param levelFile the name of level to load
    * First line define width(x) and height(y) of the game field
    * example: x=10,y=20
    * Only one tile in the field is for snake
    * w - WALL
    * g - GRASS
    * f - FOOD
    * s - SNAKE
    * @return true if successfully loaded, else false
    * */
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

    public IField getField() {
        return field;
    }

    public Snake getSnake() {
        return snake;
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    /*
    * Notifies the view about updating
    * */
    @Override
    public void notifySubscribers() {
        for (Subscriber sub : subscribers) {
            sub.handleEvent(field, coordsToRedraw, score);
        }
    }

    private IField field;
    private Snake snake;
    private int score = 0;
    private int fieldWidth;
    private int fieldHeight;
    private final List<Subscriber> subscribers = new ArrayList<>();
    private final List<ICoordinates> coordsToRedraw = new ArrayList<>();
}