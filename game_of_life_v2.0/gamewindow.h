#ifndef GAME_OF_LIFE_V2_0_GAMEWINDOW_H
#define GAME_OF_LIFE_V2_0_GAMEWINDOW_H

#include <QMainWindow>

#include "game.h"

class GameWindow : public QMainWindow
{
	Q_OBJECT
public:
	explicit GameWindow();

private slots:

private:
	Game game_;
	void createToolBar();
};

#endif //GAME_OF_LIFE_V2_0_GAMEWINDOW_H
