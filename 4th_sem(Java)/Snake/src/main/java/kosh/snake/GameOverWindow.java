package kosh.snake;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameOverWindow {
    public GameOverWindow() {
        Util.setBackground(Constants.GAME_OVER_MENU_BACK, gameOverPane);
        initScoreLabel();
        createButtons();
        controlButtons();
    }

    private void initScoreLabel() {
        scoreLabel = new Label();
        scoreLabel.setFont(new Font(Constants.LABEL_FONT_FAMILY, Constants.LABEL_FONT_SIZE));
        scoreLabel.setLayoutX(Constants.MENU_BUTTONS_START_X);
        scoreLabel.setLayoutY(Constants.MENU_BUTTONS_START_Y / 2.);
        gameOverPane.getChildren().add(scoreLabel);
    }

    public void showGameOver(Stage stage, int levelNum, int score) {
        scoreLabel.setText("Your score: " + score);
        stage.setScene(new Scene(gameOverPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT));
        stage.show();
        Platform.runLater(() -> fillRecordsTable(levelNum, score));
    }

    private void controlButtons() {
        buttons.get("back").setOnAction(event -> {
            MainMenuWindow mainWindow = new MainMenuWindow();
        });
    }

    private void createButtons() {
        SnakeButton backButton = new SnakeButton("Back");
        Util.addButtonToMenu("back", backButton, gameOverPane, buttons);
    }

    /*
    * Writes to records file of specified level number
    * Records file must be as follows: "recordsX.txt", where X is level number
    * Lines in file as follows: USERNAME:SCORE , where ":" is a delimiter specified in Constant class and SCORE is an integer
    * */
    private void fillRecordsTable(int levelNum, int score) {
        String recordsFileName = "records" + levelNum + ".txt";
        File recordsFile = new File(Constants.ABS_PATH_TO_RESOURCES + recordsFileName);
        String recordLine;
        boolean writtenNewRecord = false;
        int lineCount = 0;
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(recordsFile))) {
            while ((recordLine = reader.readLine()) != null && lineCount < Constants.NUM_RECORDS) {
                String[] recordData = recordLine.split(Constants.RECORDS_DELIMITER);
                if (score > Integer.parseInt(recordData[recordData.length - 1]) && !writtenNewRecord) {
                    writtenNewRecord = true;
                    String oldRecord = recordLine;
                    recordLine = getNewRecord(score);
                    buffer.append(recordLine).append('\n');
                    buffer.append(oldRecord).append('\n');
                } else {
                    buffer.append(recordLine).append('\n');
                }
                ++lineCount;
            }
            if (lineCount == 0) {
                recordLine = getNewRecord(score);
                buffer.append(recordLine).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeRecords(recordsFile, buffer);
    }

    private void writeRecords(File recordsFile, StringBuilder buffer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recordsFile))) {
            writer.write(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Init text input dialog asking username
    * @return recordLine the new records
    * */
    private String getNewRecord(int score) {
        String recordLine;
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("New record!!!");
        inputDialog.setHeaderText("You're have set a new record!");
        inputDialog.setContentText("Please, enter your name:");
        Optional<String> name = inputDialog.showAndWait();
        recordLine = name.orElse("Unknown") + Constants.RECORDS_DELIMITER + score;
        return recordLine;
    }

    private final Map<String, Button> buttons = new HashMap<>();
    private final Pane gameOverPane = new Pane();
    private Label scoreLabel;
}
