package kosh.snake;

public class Constants {
    public static final int TILE_WIDTH = 20;
    public static final int TILE_HEIGHT = 20;
    public static final int TILES_NUM_X = 30;
    public static final int TILES_NUM_Y = 30;
    public static final int SCALE = 1;
    public static final int WINDOW_WIDTH = TILES_NUM_X * TILE_WIDTH * SCALE;
    public static final int WINDOW_HEIGHT = TILES_NUM_Y * TILE_HEIGHT * SCALE;

    public static final int TIMEOUT = 1000000000;

    public static final int MENU_START_X = 50;
    public static final int MENU_START_Y = 100;
    public static final int MENU_BUTTONS_OFFSET = 80;
    public static final int HEIGHT_OFFSET_GAME_WINDOW = 17; //height of score label in vbox


    public static final String MAIN_MENU_BACK = "snakeBack.jpg";
    public static final String LEVELS_MENU_BACK = "snakeGreenBack.jpg";
}
