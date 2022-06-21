package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordsWindow {
    public RecordsWindow() {
        ListView<String> highScores = new ListView<>();
        Util.setBackground(Constants.RECORDS_MENU_BACK, recordsPane);
        createButtons();
        controlButtons();
        initRecordsTable(highScores);
    }

    private void fillTable(ListView<String> table) {
        List<String> records = new ArrayList<>();
        String line;
        for (int i = 1; i <= Constants.NUM_LEVELS; ++i) {
            records.add("Level " + i + ":");
            String recordsFileName = "records" + i + ".txt";
            File recordsFile = new File(Constants.ABS_PATH_TO_RESOURCES + recordsFileName);
            int countLines = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(recordsFile))) {
                while ((line = reader.readLine()) != null && countLines < Constants.NUM_RECORDS) {
                    records.add(line);
                    ++countLines;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            records.add("");
        }
        table.getItems().addAll(records);
    }

    private void initRecordsTable(ListView<String> table) {
        table.setLayoutX(Constants.MENU_BUTTONS_START_X);
        table.setLayoutY(Constants.MENU_BUTTONS_START_Y + buttons.size() * Constants.MENU_BUTTONS_OFFSET);
        fillTable(table);
        recordsPane.getChildren().add(table);
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
}
