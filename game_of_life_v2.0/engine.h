#ifndef GAME_OF_LIFE_V2_0_ENGINE_H
#define GAME_OF_LIFE_V2_0_ENGINE_H

#include <cstddef>
#include <vector>

constexpr std::size_t DEFAULT_WIDTH = 5;
constexpr std::size_t DEFAULT_HEIGHT = 5;

class Field
{
public:
	explicit Field(std::size_t width = DEFAULT_WIDTH, std::size_t height = DEFAULT_HEIGHT);

	[[nodiscard]] std::size_t get_height() const noexcept;
	[[nodiscard]] std::size_t get_width() const noexcept;
	[[nodiscard]] std::size_t count_neighbours(long long x, long long y) const noexcept;
	[[nodiscard]] bool get_cell(long long x, long long y) const noexcept;
	void set_cell(long long x, long long y, bool cell) noexcept;
	void swap(Field &other) noexcept;
private:
	std::vector<std::vector<bool>> field_;
	std::size_t height_ = DEFAULT_HEIGHT;
	std::size_t width_ = DEFAULT_WIDTH;
};

class State
{
public:
	explicit State(std::size_t width = DEFAULT_WIDTH, std::size_t height = DEFAULT_HEIGHT);
	void print_field() const noexcept;
	void play();
private:
	Field current_;
	Field next_;

	void make_next_field();
};

#endif //GAME_OF_LIFE_V2_0_ENGINE_H
