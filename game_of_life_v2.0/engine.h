#ifndef GAME_OF_LIFE_V2_0_ENGINE_H
#define GAME_OF_LIFE_V2_0_ENGINE_H

#include <cstddef>
#include <vector>

class Field
{
public:
	[[nodiscard]] std::size_t get_height() const noexcept;
	[[nodiscard]] std::size_t get_width() const noexcept;
	[[nodiscard]] std::size_t count_neighbours(int x, int y) const noexcept;
	[[nodiscard]] bool get_cell(int x, int y) const noexcept;
	void set_cell(int x, int y, bool cell) noexcept;
	void swap(Field &other) noexcept;
private:
	std::vector<std::vector<bool>> field_;
	std::size_t height_;
	std::size_t width_;
};

class State
{
public:

private:
	Field current_;
	Field next_;

	void make_next_field();
};

#endif //GAME_OF_LIFE_V2_0_ENGINE_H
