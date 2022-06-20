package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class RecordsWindow {
    public RecordsWindow() {
        Util.setBackground(Constants.RECORDS_MENU_BACK, recordsPane);
        createButtons();
        controlButtons();
    }

    public void showRecordsWindow(Stage stage) {
        stage.setScene(new Scene(recordsPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT));
        stage.show();
    }

    private void createButtons() {
        SnakeButton backButton = new SnakeButton("Back");
        Util.addButtonToMenu("back", backButton, recordsPane, buttons);
    }

    private void controlButtons() {
        buttons.get("back").setOnAction(event -> {
            MainMenuWindow mainWindow = new MainMenuWindow();
        });
    }

    private final Map<String, Button> buttons = new HashMap<>();
    private final Pane recordsPane = new Pane();
    private ListView<String> highScores;
}
