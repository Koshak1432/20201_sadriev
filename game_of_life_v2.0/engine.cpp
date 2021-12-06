#include "engine.h"

#include <cassert>

static std::size_t getToroidCoord(int i, std::size_t max) noexcept
{
	assert(0 != max);
	while (i < 0)
	{
		i += static_cast<int>(max);
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

std::size_t Field::getHeight() const noexcept
{
	return height_;
}

std::size_t Field::getWidth() const noexcept
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

Field::Field(std::size_t width, std::size_t height) : field_(height), width_(width), height_(height)
{
	std::fill(field_.begin(), field_.end(), std::vector<bool>(width, false));
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
			if (3 == neighbours && !cell)				//custom rules
			{
				next_.setCell(x, y, true);
			}
			else if ((neighbours < 2 || neighbours > 3) && cell)				//custom rules
			{
				next_.setCell(x, y, false);
			}
		}
	}
	current_.swap(next_);
}

State::State(std::size_t width, std::size_t height) : current_(width, height), next_(width, height)
{}

Field State::getField() const noexcept
{
	return current_;
}

Field &State::getField() noexcept
{
	return current_;
}




