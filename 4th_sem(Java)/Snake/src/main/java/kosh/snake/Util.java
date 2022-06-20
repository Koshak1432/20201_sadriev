package kosh.snake;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;

import java.util.Map;

public class Util {
    private static BackgroundImage createBackImage(String backName) {
        Image image = new Image(Util.class.getResource(backName).toString(), Constants.INIT_WINDOW_WIDTH,
                                Constants.INIT_WINDOW_HEIGHT, false, true);
        return new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null);
    }

    public static void setBackground(String backName, Pane pane) {
        pane.setBackground(new Background(createBackImage(backName)));
    }

    public static void addButtonToMenu(String name, Button button, Pane pane, Map<String, Button> buttons) {
        button.setLayoutX(Constants.MENU_BUTTONS_START_X);
        button.setLayoutY(Constants.MENU_BUTTONS_START_Y + buttons.size() * Constants.MENU_BUTTONS_OFFSET);
        buttons.put(name, button);
        pane.getChildren().add(button);
    }
}
