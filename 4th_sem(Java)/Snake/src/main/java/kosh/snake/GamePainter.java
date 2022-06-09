package kosh.snake;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GamePainter implements Subscriber{

    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
        try {
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Pane root = loader.getRoot();
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(Constatns.WINDOW_WIDTH + Constatns.TILE_WIDTH);
        primaryStage.setHeight(Constatns.WINDOW_HEIGHT + 2 * Constatns.TILE_HEIGHT);
        primaryStage.show();
        root.getChildren().add(canvas);
    }

    private void loadImages(String fileName, Map<String, Image> images) {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(fileName)) {
            properties.load(in);
            for (var entry : properties.entrySet()) {
                images.put(entry.getKey().toString(), new Image(getClass().getResource((String) entry.getValue()).toString(), Constatns.TILE_WIDTH,
                                                                Constatns.TILE_HEIGHT, false, false));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawByCoords(Field field, Coordinates coords) {
        switch (field.getCell(coords)) {
            case FOOD -> graphicsContext.drawImage(images.get("Food"), coords.x() * Constatns.TILE_WIDTH, coords.y() * Constatns.TILE_HEIGHT);
            case WALL -> graphicsContext.drawImage(images.get("Wall"), coords.x() * Constatns.TILE_WIDTH, coords.y() * Constatns.TILE_HEIGHT);
            case SNAKE -> graphicsContext.drawImage(images.get("Snake"), coords.x() * Constatns.TILE_WIDTH, coords.y() * Constatns.TILE_HEIGHT);
            case EMPTY -> graphicsContext.drawImage(grassCoordsImages.get(coords), coords.x() * Constatns.TILE_WIDTH, coords.y() * Constatns.TILE_HEIGHT);
        }
    }

    GamePainter() {
        loadImages("images.properties", images);
        loadImages("grass.properties", grassImages);
    }
    private void putToGrassCoords(Coordinates coords, Random random) {
        Image image = grassImages.get("Grass" + random.nextInt(grassImages.size()));
        grassCoordsImages.put(coords, image);
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

    public void update(Field field, List<Coordinates> coordsToRedraw) {
        for (Coordinates coords : coordsToRedraw) {
            drawByCoords(field, coords);
        }
        coordsToRedraw.clear();
    }

    @Override
    public void handleEvent(Field field, List<Coordinates> coordsToRedraw) {
        update(field, coordsToRedraw);
    }

    private final Canvas canvas = new Canvas(Constatns.WINDOW_WIDTH, Constatns.WINDOW_HEIGHT);
    private final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, Image> grassImages = new HashMap<>();
    private final Map<Coordinates, Image> grassCoordsImages = new HashMap<>();
}
