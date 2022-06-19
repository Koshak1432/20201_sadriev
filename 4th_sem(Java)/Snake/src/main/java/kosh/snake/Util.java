package kosh.snake;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
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
        stage.show();
    }

    public static BackgroundImage createBackImage(String backName) {
        Image image = new Image(Util.class.getResource(backName).toString(), Constants.INIT_WINDOW_WIDTH,
                                Constants.INIT_WINDOW_HEIGHT, false, true);
        return new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null);
    }
}
