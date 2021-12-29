#ifndef GAME_OF_LIFE_V2_0_GAME_H
#define GAME_OF_LIFE_V2_0_GAME_H

#include <QWidget>

#include "engine.h"

class QScrollArea;
class RenderArea;

class Game : public QWidget
{
	Q_OBJECT

public:
	explicit Game(State state = State(), QWidget *parent = nullptr);
	QScrollArea *getScrollArea() noexcept;
	void setState(State state);

	State &getState();
	~Game() override = default;

public slots:
	void play();
	void pause();
	void clear();
	void changeSpeed(int newSpeed);
	void changeBirthRule(int idx, bool checked);
	void changeSurvivalRule(int idx, bool checked);
	void changeWidth(int newWidth);
	void changeHeight(int newHeight);

private slots:
	void gameUpdate();

private:
	State state_;
	QScrollArea *scrollArea_;
	QTimer *timer_;
	bool isPlaying = false;
};


#endif //GAME_OF_LIFE_V2_0_GAME_H
