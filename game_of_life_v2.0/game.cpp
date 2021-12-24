#include "game.h"

#include <QTimer>
#include <QScrollArea>

#include "renderarea.h"

namespace
{
	constexpr int SEC = 1000;
	constexpr int DEFAULT_SPEED = 1;
}

Game::Game(State state, QWidget *parent)
			: QWidget(parent), state_(std::move(state)), scrollArea_(new QScrollArea()), timer_(new QTimer())
{
	scrollArea_->setBackgroundRole(QPalette::Dark);
	scrollArea_->setWidget(new RenderArea(state_.getCurrent()));
	scrollArea_->setAlignment(Qt::AlignVCenter | Qt::AlignHCenter);
	timer_->setInterval(SEC / DEFAULT_SPEED);
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

void Game::setState(State state)
{
	state_ = std::move(state);
	RenderArea render(state_.getCurrent());
	scrollArea_->setWidget(new RenderArea(state_.getCurrent()));
}

void Game::changeSpeed(int newSpeed)
{
	timer_->setInterval(SEC / newSpeed);
}

State &Game::getState()
{
	return state_;
}

void Game::clear()
{
	state_.clear();
	gameUpdate();
}
