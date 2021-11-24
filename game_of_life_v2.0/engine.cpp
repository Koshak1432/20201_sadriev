#include "engine.h"

#include <cassert>
#include <iostream>
#include <ctime> //todo delete in release

static std::size_t get_toroid_coord(long long i, std::size_t max) noexcept
{
	assert(0 != max);
	while (i < 0)
	{
		i += static_cast<long long>(max);
	}
	return i % max;
}

bool Field::get_cell(long long x, long long y) const noexcept
{
	return field_[get_toroid_coord(y, height_)][get_toroid_coord(x, width_)];
}

std::size_t Field::count_neighbours(long long x, long long y) const noexcept
{
	std::size_t neighbours = 0;
	for (long long i = x - 1; i <= x + 1; ++i)
	{
		for (long long j = y - 1; j <= y + 1; ++j)
		{
			if ((x == i) && (y == j))
			{
				continue;
			}
			if (get_cell(i, j))
			{
				++neighbours;
			}
		}
	}
	return neighbours;
}

std::size_t Field::get_height() const noexcept
{
	return height_;
}

std::size_t Field::get_width() const noexcept
{
	return width_;
}

void Field::set_cell(long long x, long long y, bool cell) noexcept
{
	field_[get_toroid_coord(y, height_)][get_toroid_coord(x, width_)] = cell;
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

void State::make_next_field()
{
	for (std::size_t x = 0; x < current_.get_width(); ++x)
	{
		for (std::size_t y = 0; y < current_.get_height(); ++y)
		{ 	//todo remove static cast ???????????????????????????????
			std::size_t neighbours = current_.count_neighbours(static_cast<long long>(x), static_cast<long long>(y));
			bool cell = current_.get_cell(static_cast<long long>(x), static_cast<long long>(y));
			next_.set_cell(static_cast<long long>(x), static_cast<long long>(y), cell);
			if (3 == neighbours && !cell)				//custom rules
			{
				next_.set_cell(static_cast<long long>(x), static_cast<long long>(y), true);
			}
			else if ((neighbours < 2 || neighbours > 3) && cell)				//custom rules
			{
				next_.set_cell(static_cast<long long>(x), static_cast<long long>(y), false);
			}
		}
	}
	current_.swap(next_);
}

void State::print_field() const noexcept
{
	std::cout << "-------------------------------" << std::endl;
	for (std::size_t x = 0; x < current_.get_width(); ++x)
	{
		for (std::size_t y = 0; y < current_.get_height(); ++y)
		{
			current_.get_cell(static_cast<long long>(x), static_cast<long long>(y)) ? (std::cout << "+") : (std::cout << "-");
		}
		std::cout << std::endl;
	}

	for (long long x = 0; x < current_.get_width(); ++x)
	{
		for (long long y = 0; y < current_.get_height(); ++y)
		{
			std::cout << current_.count_neighbours(x, y);
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
			print_field();
			make_next_field();
		}
		else
		{
			break;
		}
	}
}

State::State(std::size_t width, std::size_t height) : current_(width, height), next_(width, height)
{}




