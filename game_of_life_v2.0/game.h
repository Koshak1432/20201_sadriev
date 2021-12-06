#ifndef GAME_OF_LIFE_V2_0_GAME_H
#define GAME_OF_LIFE_V2_0_GAME_H

#include <QWidget>
#include "renderarea.h"
#include "engine.h"

constexpr int DEFAULT_SPEED = 250;

class Game : public QWidget
{
public:
	explicit Game(int speed = DEFAULT_SPEED, QWidget *parent = nullptr);
	RenderArea *getRenderArea() noexcept;

public slots:
	void play();
	void pause();

private slots:
	void gameUpdate();

private:
	State state_;
	RenderArea *renderArea_;
	QTimer *timer_;
	int speed_ = DEFAULT_SPEED;
	bool isPlaying = false;

};


#endif //GAME_OF_LIFE_V2_0_GAME_H
