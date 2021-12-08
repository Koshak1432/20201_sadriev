#include "game.h"

#include <QTimer>
#include <QScrollArea>

#include "renderarea.h"

Game::Game(State state, int speed, QWidget *parent)
			: QWidget(parent), state_(std::move(state)), scrollArea_(new QScrollArea()), timer_(new QTimer()), speed_(speed)
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

RenderArea *Game::getRenderArea()
{
	return dynamic_cast<RenderArea *>(scrollArea_->widget());
}

Game &Game::operator =(Game &&other) noexcept
{
	if (&other != this)
	{
		state_ = std::move(other.state_);
		scrollArea_ = other.scrollArea_;
		timer_ = other.timer_;
		speed_ = other.speed_;
		isPlaying = other.isPlaying;
	}
	return *this;
}
