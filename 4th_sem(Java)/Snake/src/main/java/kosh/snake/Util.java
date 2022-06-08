package kosh.snake;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Util {
    public static void changeStage(String sceneName, Stage stage) {
        FXMLLoader loader = new FXMLLoader(Util.class.getResource(sceneName));
        try {
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
