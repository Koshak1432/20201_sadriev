#ifndef GAME_OF_LIFE_V2_0_GAME_H
#define GAME_OF_LIFE_V2_0_GAME_H

#include <QWidget>
#include "engine.h"

class QScrollArea;

constexpr int DEFAULT_SPEED = 250;


class Game : public QWidget
{
public:
	explicit Game(int speed = DEFAULT_SPEED, QWidget *parent = nullptr);
	QScrollArea *getScrollArea() noexcept;

public slots:
	void play();
	void pause();

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
