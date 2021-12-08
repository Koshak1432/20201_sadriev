#ifndef GAME_OF_LIFE_V2_0_GAMEWINDOW_H
#define GAME_OF_LIFE_V2_0_GAMEWINDOW_H

#include <QMainWindow>

#include "game.h"
#include "io.h"

class GameWindow : public QMainWindow
{
	Q_OBJECT
public:
	explicit GameWindow();

private slots:
	void open();


private:
	Game game_;

	void createToolBar();
	void loadFile(const QString &fileName);
};

#endif //GAME_OF_LIFE_V2_0_GAMEWINDOW_H
