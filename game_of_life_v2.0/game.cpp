#include "game.h"

#include <QTimer>
#include <QScrollArea>

#include "renderarea.h"

constexpr int SEC = 1000;

Game::Game(State state, int speed, QWidget *parent)
			: QWidget(parent), state_(std::move(state)), scrollArea_(new QScrollArea()), timer_(new QTimer()), speed_(speed)
{
	scrollArea_->setBackgroundRole(QPalette::Dark);
	scrollArea_->setWidget(new RenderArea(state_.getCurrent()));
	scrollArea_->setAlignment(Qt::AlignVCenter | Qt::AlignHCenter);
	timer_->setInterval(SEC / speed);
	connect(timer_, &QTimer::timeout, this, &Game::gameUpdate);
}

void Game::play()
{
	if (isPlaying)
	{
		return;
	}
	isPlaying = true;
	timer_->start();
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

RenderArea *Game::getRenderArea()
{
	return dynamic_cast<RenderArea *>(scrollArea_->widget());
}

void Game::setState(State state)
{
	state_ = std::move(state);
	scrollArea_->setWidget(new RenderArea(state_.getCurrent()));
}

void Game::changeSpeed(int newSpeed)
{
	speed_ = newSpeed;
	timer_->setInterval(SEC / newSpeed);
}
