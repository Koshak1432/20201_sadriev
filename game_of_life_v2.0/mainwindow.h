#ifndef GAME_OF_LIFE_V2_0_MAINWINDOW_H
#define GAME_OF_LIFE_V2_0_MAINWINDOW_H

#include <QWidget>

class RenderArea;
class Field;


class Window : public QWidget
{
	Q_OBJECT
public:
	explicit Window(Field field);

private:
	RenderArea *renderArea;
};


#endif //GAME_OF_LIFE_V2_0_MAINWINDOW_H
