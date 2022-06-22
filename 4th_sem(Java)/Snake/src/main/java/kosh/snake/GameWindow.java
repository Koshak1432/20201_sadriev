package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameWindow implements Subscriber {
    public GameWindow(int fieldWidth, int fieldHeight) {
        loadImages("images.properties", images);
        loadImages("grass.properties", grassImages);
        int windowWidth = fieldWidth * Constants.TILE_WIDTH;
        int windowHeight = fieldHeight * Constants.TILE_HEIGHT;
        initWindow(windowWidth, windowHeight);
    }

    private void initWindow(int windowWidth, int windowHeight) {
        scoreLabel = new Label();
        scoreLabel.setFont(new Font(Constants.LABEL_FONT_FAMILY, Constants.LABEL_FONT_SIZE));
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();
        VBox layout = new VBox(scoreLabel, canvas);
        Pane gamePane = new Pane();
        gameScene = new Scene(gamePane, windowWidth, windowHeight + Constants.HEIGHT_OFFSET_GAME_WINDOW);
        gamePane.getChildren().add(layout);
    }

    public void showGame(Stage menuStage) {
        menuStage.setScene(gameScene);
        menuStage.setResizable(false);
        menuStage.show();
    }

    /*
    * Loads field's images to map
    * @param fileName name of property file
    * @param images a Map<String, Image> where to load images
    * */
    private void loadImages(String fileName, Map<String, Image> images) {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(fileName)) {
            properties.load(in);
            for (var entry : properties.entrySet()) {
                images.put(entry.getKey().toString(), new Image(
                        Objects.requireNonNull(getClass().getResource((String) entry.getValue())).toString(), Constants.TILE_WIDTH,
                        Constants.TILE_HEIGHT, false, false));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawByCoords(IField field, ICoordinates coords) {
        switch (field.getCell(coords)) {
            case FOOD -> graphicsContext.drawImage(images.get("Food"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case WALL -> graphicsContext.drawImage(images.get("Wall"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case SNAKE -> graphicsContext.drawImage(images.get("Snake"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case GRASS -> graphicsContext.drawImage(grassCoordsImages.get(coords), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
        }
    }

    /*
    * Assigns random grass sprite to coordinates and put it to grass map
    * @param coords coordinates to assign
    * */
    private void putToGrassCoords(ICoordinates coords, Random random) {
        grassCoordsImages.put(coords, grassImages.get("Grass" + random.nextInt(grassImages.size())));
    }

    public void drawInitialField(IField field) {
        Random random = new Random();
        for (int y = 0; y < field.getHeight(); ++y) {
            for (int x = 0; x < field.getWidth(); ++x) {
                Coordinates coords = new Coordinates(x, y);
                putToGrassCoords(coords, random);
                drawByCoords(field, coords);
            }
        }
    }

    private void drawScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /*
    * Updates field
    * */
    public void update(IField field, List<ICoordinates> coordsToRedraw, int score) {
        for (ICoordinates coords : coordsToRedraw) {
            drawByCoords(field, coords);
        }
        drawScore(score);
        coordsToRedraw.clear();
    }

    @Override
    public void handleEvent(IField field, List<ICoordinates> coordsToRedraw, int score) {
        update(field, coordsToRedraw, score);
    }

    private GraphicsContext graphicsContext;
    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, Image> grassImages = new HashMap<>();
    private final Map<ICoordinates, Image> grassCoordsImages = new HashMap<>();
    private Label scoreLabel;
    private Scene gameScene;
}
