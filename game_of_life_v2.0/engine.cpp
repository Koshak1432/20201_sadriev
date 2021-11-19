#include "engine.h"

#include <cassert>

static std::size_t get_toroid_coord(int i, std::size_t max) noexcept
{
	assert(0 != max);
	while (i < 0)
	{
		i += static_cast<int>(max);
	}
	return i % max;
}

bool Field::get_cell(int x, int y) const noexcept
{
	return field_[get_toroid_coord(y, height_)][get_toroid_coord(x, width_)];
}

std::size_t Field::count_neighbours(int x, int y) const noexcept
{
	std::size_t neighbours = 0;
	for (int i = x - 1; i <= x + 1; ++i)
	{
		for (int j = y - 1; j <= y + 1; ++j)
		{
			if ((x != i) && (y != j))
			{
				if (get_cell(i, j))
				{
					++neighbours;
				}
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

void Field::set_cell(int x, int y, bool cell) noexcept
{
	field_[get_toroid_coord(y, height_)][get_toroid_coord(x, width_)] = cell;
}

void Field::swap(Field &other) noexcept
{
	std::swap(height_, other.height_);
	std::swap(width_, other.width_);
	field_.swap(other.field_);
}

void State::make_next_field()
{
	for (std::size_t x = 0; x < current_.get_width(); ++x)
	{
		for (std::size_t y = 0; y < current_.get_height(); ++y)
		{ 	//todo remove static cast ???????????????????????????????
			std::size_t neighbours = current_.count_neighbours(static_cast<int>(x), static_cast<int>(y));
			bool cell = current_.get_cell(static_cast<int>(x), static_cast<int>(y));
			next_.set_cell(static_cast<int>(x), static_cast<int>(y), cell);
			if (3 == neighbours && !cell)
			{
				next_.set_cell(static_cast<int>(x), static_cast<int>(y), true);
			}
			else if ((neighbours < 2 || neighbours > 3) && cell)
			{
				next_.set_cell(static_cast<int>(x), static_cast<int>(y), false);
			}
		}
	}
	current_.swap(next_);
}


