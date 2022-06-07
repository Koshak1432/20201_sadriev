package kosh.snake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GamePainter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
        try {
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Pane root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
        root.getChildren().add(canvas);
    }
    private void drawBackground(Field field) {
        Random random = new Random();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                graphicsContext.drawImage(backgroundSprites.get(random.nextInt(backgroundSprites.size())), tileSize * x, tileSize * y);
            }
        }
    }

    private void drawField(Field field) {
        drawBackground(field);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
            String name = switch (field.getCell(new Coordinates(x, y))) {
                case FOOD -> "/food_sprite.png";
                case WALL -> "/wall_sprite.png";
                case SNAKE -> "snake_sprite.png";
                case EMPTY -> null;
            };
            if (name != null) {
                graphicsContext.drawImage(new Image(getClass().getResource(name).toString()), tileSize * x, tileSize * y);
            }
            }
        }
    }

    private Stage stage;
    private final int tileSize = 20;
    private final int tilesX = 30;
    private final int tilesY = 30;
    private final int width = tilesX * tileSize;
    private final int height = tilesY * tileSize;
    private Canvas canvas = new Canvas(width, height);
    private GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private ArrayList<Image> backgroundSprites;


}
