package kosh.snake;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;

import java.util.Objects;

public class SnakeButton extends Button {

    public SnakeButton(String text) {
        setText(text);
        setPrefHeight(45);
        setPrefWidth(190);
        initBackImages();
        setBackground(buttonReleasedBackground);
        initListeners();
    }

    private void setButtonPressedStyle() {
        setBackground(buttonPressedBackground);
        setPrefHeight(49);
        setLayoutY(getLayoutY() - 4);
    }

    private void setButtonReleasedStyle() {
        setBackground(buttonReleasedBackground);
        setPrefHeight(45);
        setLayoutY(getLayoutY() + 4);
    }

    private void initListeners() {
        setOnMousePressed(event -> setButtonPressedStyle());
        setOnMouseReleased(event -> setButtonReleasedStyle());
        setOnMouseEntered(event -> setEffect(new DropShadow()));
        setOnMouseExited(event -> setEffect(null));
    }

    private void initBackImages() {
        Image releasedImage = new Image(Objects.requireNonNull(getClass().getResource("freeButton.png")).toString());
        Image pressedImage = new Image(Objects.requireNonNull(getClass().getResource("pressedButton.png")).toString());
        BackgroundImage backgroundImageReleased = new BackgroundImage(releasedImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null);
        BackgroundImage backgroundImagePressed = new BackgroundImage(pressedImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null);
        buttonReleasedBackground = new Background(backgroundImageReleased);
        buttonPressedBackground = new Background(backgroundImagePressed);
    }

    private Background buttonPressedBackground;
    private Background buttonReleasedBackground;
}
