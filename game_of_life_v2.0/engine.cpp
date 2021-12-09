#include "engine.h"

#include <cassert>

static std::size_t getToroidCoord(int i, std::size_t max) noexcept
{
	assert(0 != max);
	while (i < 0)
	{
		i += int(max);
	}
	return i % max;
}

bool Field::getCell(int x, int y) const noexcept
{
	return field_[getToroidCoord(y, height_)][getToroidCoord(x, width_)];
}

std::size_t Field::countNeighbours(int x, int y) const noexcept
{
	std::size_t neighbours = 0;
	for (int i = x - 1; i <= x + 1; ++i)
	{
		for (int j = y - 1; j <= y + 1; ++j)
		{
			if ((x == i) && (y == j))
			{
				continue;
			}
			if (getCell(i, j))
			{
				++neighbours;
			}
		}
	}
	return neighbours;
}

int Field::getHeight() const noexcept
{
	return height_;
}

int Field::getWidth() const noexcept
{
	return width_;
}

void Field::setCell(int x, int y, bool cell) noexcept
{
	field_[getToroidCoord(y, height_)][getToroidCoord(x, width_)] = cell;
}

void Field::swap(Field &other) noexcept
{
	std::swap(height_, other.height_);
	std::swap(width_, other.width_);
	field_.swap(other.field_);
}

Field::Field(int width, int height) : field_(height), width_(width), height_(height)
{
	std::fill(field_.begin(), field_.end(), std::vector<bool>(width, false));
}

Field::Field(Field &&other) noexcept : field_(std::move(other.field_)), height_(other.height_), width_(other.width_)
{}

Field &Field::operator =(Field &&other) noexcept
{
	if (&other != this)
	{
		field_ = std::move(other.field_);
		height_ = other.height_;
		width_ = other.width_;
	}
	return *this;
}

void State::makeNextField()
{
	for (int x = 0; x < current_.getWidth(); ++x)
	{
		for (int y = 0; y < current_.getHeight(); ++y)
		{
			std::size_t neighbours = current_.countNeighbours(x, y);
			bool cell = current_.getCell(x, y);
			next_.setCell(x, y, cell);
			if (rules_.birth_[neighbours] && !cell)
			{
				next_.setCell(x, y, true);
			}
			else if (!rules_.sustain_[neighbours] && cell)
			{
				next_.setCell(x, y, false);
			}
		}
	}
	current_.swap(next_);
}

State::State(Rules rules, int width, int height) noexcept: current_(width, height), next_(width, height), rules_(std::move(rules))
{}

State::State(State &&other) noexcept : current_(std::move(other.current_)), next_(std::move(other.next_)), rules_(std::move(other.rules_))
{}

State &State::operator =(State &&other) noexcept
{
	if (&other != this)
	{
		current_ = std::move(other.current_);
		next_ = std::move(other.next_);
		rules_ = std::move(other.rules_);
	}
	return *this;
}

int State::getWidth() noexcept
{
	return current_.getWidth();
}

int State::getHeight() noexcept
{
	return current_.getHeight();
}

Field &State::getCurrent() noexcept
{
	return current_;
}

Rules::Rules() noexcept : birth_(9, false), sustain_(9, false)
{
	birth_[3] = true;
	sustain_[2] = true;
	sustain_[3] = true;
}

Rules::Rules(std::vector<bool> birth, std::vector<bool> sustain) noexcept : birth_(std::move(birth)), sustain_(std::move(sustain))
{}

Rules::Rules(Rules &&other) noexcept :birth_(std::move(other.birth_)), sustain_(std::move(other.sustain_))
{}

Rules &Rules::operator =(Rules &&other) noexcept
{
	if (&other != this)
	{
		birth_ = std::move(other.birth_);
		sustain_ = std::move(other.sustain_);
	}
	return *this;
}
