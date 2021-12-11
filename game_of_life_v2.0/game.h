#ifndef GAME_OF_LIFE_V2_0_GAME_H
#define GAME_OF_LIFE_V2_0_GAME_H

#include <QWidget>

#include "engine.h"

class QScrollArea;
class RenderArea;

constexpr int DEFAULT_SPEED = 1;

class Game : public QWidget
{
	Q_OBJECT
public:
	explicit Game(State state = State(), int speed = DEFAULT_SPEED, QWidget *parent = nullptr);
	QScrollArea *getScrollArea() noexcept;
	RenderArea *getRenderArea();
	void setState(State state);
	State &getState();
public slots:
	void play();
	void pause();
	void changeSpeed(int newSpeed);

private slots:
	void gameUpdate();

private:
	State state_;
	QScrollArea *scrollArea_;
	QTimer *timer_;
	int speed_ = DEFAULT_SPEED;
	bool isPlaying = false;
};


#endif //GAME_OF_LIFE_V2_0_GAME_H
