package kosh.snake;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainMenuWindow {

    public MainMenuWindow() {
        menuStage.setTitle("Snake");
        menuStage.setScene(menuScene);
        createBackground(Constants.MAIN_MENU_BACK);
        createButtons();
        controlButtons();
    }

    private void controlButtons() {
        menuButtons.get("start").setOnAction(event -> {
            LevelsWindow levelsWindow = new LevelsWindow();
            levelsWindow.showLevelsStage(menuStage);

//            GameWindow gameWindow = new GameWindow();
//            gameWindow.createNewGame(menuStage);

        });

        menuButtons.get("records").setOnAction(event -> {
            //todo
//            RecordsWindow recordsWindow = new RecordsWindow();
//            recordsWindow.showRecords();
        });

        menuButtons.get("exit").setOnAction(event -> menuStage.close());
    }

    private void createBackground(String backName) {
        menuPane.setBackground(new Background(Util.createBackImage(backName)));
    }

    public static Stage getMenuStage() {
        return menuStage;
    }

//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        stage = primaryStage;
//        stage.setTitle("Snake");
//        FXMLLoader loader = new FXMLLoader(Util.class.getResource("MainScene.fxml"));
//        try {
//            loader.load();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        Pane root = (Pane) loader.getRoot();
//        Image backgroundImage = new Image(getClass().getResource("snakeBack.jpg").toString(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, true, false);
//        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
//                                                         null, null);
//        root.setBackground(new Background(background));
//        stage.setScene(new Scene(root));
//        stage.show();
////        Util.changeStage("MainScene.fxml", stage);
//    }

//    @FXML
//    void initialize() {
//        startButton.setOnAction(event -> {
//            LevelsController levelsController = new LevelsController();
//            levelsController.start(stage);
//        });
//        recordsButton.setOnAction(event -> Util.changeStage("RecordsScene.fxml", stage));
//        exitButton.setOnAction(event -> stage.close());
//    }
    private void addButtonToMenu(String name, Button button) {
        button.setLayoutX(Constants.MENU_START_X);
        button.setLayoutY(Constants.MENU_START_Y + menuButtons.size() * Constants.MENU_BUTTONS_OFFSET);
        menuButtons.put(name, button);
        menuPane.getChildren().add(button);
    }

    private void createButtons() {
        SnakeButton startButton = new SnakeButton("Start");
        SnakeButton recordsButton = new SnakeButton("Records");
        SnakeButton exitButton = new SnakeButton("Exit");
        addButtonToMenu("start", startButton);
        addButtonToMenu("records", recordsButton);
        addButtonToMenu("exit", exitButton);
    }

    private Pane menuPane = new Pane();
    private Scene menuScene = new Scene(menuPane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    private static final Stage menuStage = new Stage();
    private Map<String, Button> menuButtons = new HashMap<>();
}
