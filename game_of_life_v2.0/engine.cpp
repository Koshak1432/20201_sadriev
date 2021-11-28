#include "engine.h"

#include <cassert>
#include <iostream>
#include <ctime> //todo: delete in release

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
	srand(993);
	field_.reserve(height);
	std::vector<bool> row(width);
	row.reserve(width);
	for (std::size_t i = 0; i < height; ++i)
	{
		for (std::size_t j = 0; j < width; ++j)
		{
			(0 == rand() % 2) ? row[j] = true : row[j] = false;
		}
		field_[i] = row;
	}
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

void State::printField() const noexcept
{
	std::cout << "-------------------------------" << std::endl;
	for (int x = 0; x < current_.getWidth(); ++x)
	{
		for (int y = 0; y < current_.getHeight(); ++y)
		{
			current_.getCell(x, y) ? (std::cout << "+") : (std::cout << "-");
		}
		std::cout << std::endl;
	}

	for (int x = 0; x < current_.getWidth(); ++x)
	{
		for (int y = 0; y < current_.getHeight(); ++y)
		{
			std::cout << current_.countNeighbours(x, y);
		}
		std::cout << std::endl;
	}
	std::cout << "-------------------------------" << std::endl << std::endl;
}

void State::play()
{
	while (true)
	{
		std::string str;
		std::cin >> str;
		if ("quit" != str)
		{
//			printField();
			makeNextField();
		}
		else
		{
			break;
		}
	}
}

State::State(std::size_t width, std::size_t height) : current_(width, height), next_(width, height)
{}

Field State::getField() const noexcept
{
	return current_;
}




