#include "game.h"

#include <QTimer>
#include <QScrollArea>

#include "renderarea.h"
Game::Game(int speed, QWidget *parent)
			: QWidget(parent), state_(), scrollArea_(new QScrollArea()), timer_(new QTimer()), speed_(speed)
{
	scrollArea_->setBackgroundRole(QPalette::Dark);
	scrollArea_->setWidget(new RenderArea(state_.getField()));
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

	scrollArea_->widget()->update();
}

QScrollArea *Game::getScrollArea() noexcept
{
	return scrollArea_;
}
