#include "game.h"

#include <QTimer>

Game::Game(int speed, QWidget *parent) : QWidget(parent), state_(), renderArea_(new RenderArea(state_.getField())), timer_(new QTimer()), speed_(speed)
{
	connect(timer_, &QTimer::timeout, this, &Game::gameUpdate);
}

void Game::play()
{
	if (isPlaying)
	{
		return;
	}
	isPlaying = true;
	timer_->start(speed_);
}

void Game::pause()
{
	isPlaying = false;
	timer_->stop();
}

void Game::gameUpdate()
{
	state_.makeNextField();

	renderArea_->update();
}

RenderArea *Game::getRenderArea() noexcept
{
	return renderArea_;
}
