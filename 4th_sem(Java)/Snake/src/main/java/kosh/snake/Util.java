package kosh.snake;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
    }
}
