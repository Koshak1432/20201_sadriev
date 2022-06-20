package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
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

    public void showGameOver(Stage stage, int score) {
        scoreLabel.setText("Your score: " + score);
//        fillRecordsTable();
        stage.setScene(new Scene(gameOverPane, Constants.INIT_WINDOW_WIDTH, Constants.INIT_WINDOW_HEIGHT));
        stage.show();
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

    private void fillRecordsTable(int levelNum) {
        String recordsFile = "records" + levelNum + ".txt";
        boolean newRecord = false;
        String oldRecord = "";
        String recordLine;
        StringBuilder buffer = new StringBuilder();
        try (InputStream in = getClass().getResourceAsStream(recordsFile)) {
            assert in != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                while ((recordLine = reader.readLine()) != null) {
                    String[] recordData = recordLine.split(":");
                    if (!newRecord) {
                        if (Integer.parseInt(recordData[recordData.length - 1]) > score) {
                            newRecord = true;
                            oldRecord = recordLine;
//todo swap properly
                            TextInputDialog inputDialog = new TextInputDialog();
                            inputDialog.setTitle("New record!!!");
                            inputDialog.setHeaderText("You're have set a new record!");
                            inputDialog.setContentText("Please, enter your name:");
                            Optional<String> name = inputDialog.showAndWait();
                            recordLine = name.orElse("Unknown") + ":" + score;
                        }
                    } else {
                        String tmp = recordLine;
                        recordLine = oldRecord;
                        oldRecord = tmp;
                    }
                    buffer.append(recordLine).append('\n');
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(recordsFile)){
            out.write(buffer.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, Button> buttons = new HashMap<>();
    private final Pane gameOverPane = new Pane();
    private Label scoreLabel;
    private int score;
}
