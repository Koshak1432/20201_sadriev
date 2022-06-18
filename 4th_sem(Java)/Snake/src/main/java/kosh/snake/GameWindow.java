package kosh.snake;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameWindow implements Subscriber {
    public GameWindow() {
        loadImages("images.properties", images);
        loadImages("grass.properties", grassImages);
        VBox layout = new VBox(scoreLabel, canvas);
        gameScene = new Scene(gamePane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT + Constants.HEIGHT_OFFSET_GAME_WINDOW);
        gamePane.getChildren().add(layout);
    }

    public void showGame(Stage menuStage) {
        menuStage.setScene(gameScene);
        menuStage.show();
        System.out.println("SHOWN game stage");
    }

    private void loadImages(String fileName, Map<String, Image> images) {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(fileName)) {
            properties.load(in);
            for (var entry : properties.entrySet()) {
                images.put(entry.getKey().toString(), new Image(getClass().getResource((String) entry.getValue()).toString(), Constants.TILE_WIDTH,
                                                                Constants.TILE_HEIGHT, false, false));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawByCoords(Field field, Coordinates coords) {
        switch (field.getCell(coords)) {
            case FOOD -> graphicsContext.drawImage(images.get("Food"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case WALL -> graphicsContext.drawImage(images.get("Wall"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case SNAKE -> graphicsContext.drawImage(images.get("Snake"), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
            case EMPTY -> graphicsContext.drawImage(grassCoordsImages.get(coords), coords.x() * Constants.TILE_WIDTH, coords.y() * Constants.TILE_HEIGHT);
        }
    }

    private void putToGrassCoords(Coordinates coords, Random random) {
        grassCoordsImages.put(coords, grassImages.get("Grass" + random.nextInt(grassImages.size())));
    }

    public void drawInitialField(Field field) {
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

    public void update(Field field, List<Coordinates> coordsToRedraw, int score) {
        for (Coordinates coords : coordsToRedraw) {
            drawByCoords(field, coords);
        }
        drawScore(score);
        coordsToRedraw.clear();
    }

    @Override
    public void handleEvent(Field field, List<Coordinates> coordsToRedraw, int score) {
        update(field, coordsToRedraw, score);
    }

    private final Canvas canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    private final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, Image> grassImages = new HashMap<>();
    private final Map<Coordinates, Image> grassCoordsImages = new HashMap<>();
//    VBox layout = new VBox(scoreLabel, canvas);

    private final Label scoreLabel = new Label();
    private final Pane gamePane = new Pane();
    private final Scene gameScene;
}
