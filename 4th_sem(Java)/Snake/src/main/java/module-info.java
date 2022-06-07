module kosh.snake {
    requires javafx.controls;
    requires javafx.fxml;


    opens kosh.snake to javafx.fxml;
    exports kosh.snake;
}