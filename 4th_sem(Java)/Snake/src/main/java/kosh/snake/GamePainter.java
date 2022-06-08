package kosh.snake;

import javafx.application.Application;
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
        primaryStage.show();
        root.getChildren().add(canvas);
    }

    private void loadImages(String fileName, Map<String, Image> images) {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(fileName)) {
            properties.load(in);
            for (var entry : properties.entrySet()) {
                images.put(entry.getKey().toString(), new Image(getClass().getResource((String) entry.getValue()).toString()));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GamePainter() {
        loadImages("images.properties", images);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void drawBackground(Field field) {
        Random random = new Random();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Image image = images.get("Grass" + random.nextInt(countGrassSprites));
                grassImages.put(new Coordinates(x, y), image);
                graphicsContext.drawImage(image, tileSize * x, tileSize * y);
            }
        }
    }

    public void drawInitialField(Field field) {
//        Random random = new Random();
        drawBackground(field);
        update(field);
    }

    public void update(Field field) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Coordinates coords = new Coordinates(x, y);
                switch (field.getCell(coords)) {
                    case FOOD -> graphicsContext.drawImage(images.get("Food"), tileSize * x, tileSize * y);
                    case WALL -> graphicsContext.drawImage(images.get("Wall"), tileSize * x, tileSize * y);
                    case SNAKE -> graphicsContext.drawImage(images.get("Snake"), tileSize * x, tileSize * y);
                    case EMPTY -> graphicsContext.drawImage(grassImages.get(coords), tileSize * x, tileSize * y);
                }
            }
        }
    }

    @Override
    public void handleEvent(Field field) {
        update(field);
    }

    private final int tileSize = 20;
    private final int tilesX = 30;
    private final int tilesY = 30;
    private final int width = tilesX * tileSize;
    private final int height = tilesY * tileSize;
    private final Canvas canvas = new Canvas(width, height);
    private final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private final Map<String, Image> images = new HashMap<>();
    private final Map<Coordinates, Image> grassImages = new HashMap<>();
    private final int countGrassSprites = 6;
}
