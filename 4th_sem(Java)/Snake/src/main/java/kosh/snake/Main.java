package kosh.snake;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindow view = new MainWindow();
            primaryStage = view.getMenuStage();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
