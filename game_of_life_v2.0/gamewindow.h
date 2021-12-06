#ifndef GAME_OF_LIFE_V2_0_GAMEWINDOW_H
#define GAME_OF_LIFE_V2_0_GAMEWINDOW_H

#include <QMainWindow>

#include "game.h"

class GameWindow : public QMainWindow
{
	Q_OBJECT
public:
	explicit GameWindow();

private:
	Game game_;

//	QAction *playAction;
//	QAction *pauseAction;
//	QAction *saveAction;
//	QAction *loadAction;

	void createActions();
};

#endif //GAME_OF_LIFE_V2_0_GAMEWINDOW_H
