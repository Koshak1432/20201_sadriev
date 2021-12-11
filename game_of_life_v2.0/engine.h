#ifndef GAME_OF_LIFE_V2_0_ENGINE_H
#define GAME_OF_LIFE_V2_0_ENGINE_H

#include <vector>

constexpr int DEFAULT_WIDTH = 80;
constexpr int DEFAULT_HEIGHT = 40;

class Field
{
public:
	explicit Field(int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT); //check > 0 ??????

	Field(Field &&other) noexcept;
	Field(const Field &other) = default;
	[[nodiscard]] int getHeight() const noexcept;
	[[nodiscard]] int getWidth() const noexcept;
	[[nodiscard]] std::size_t countNeighbours(int x, int y) const noexcept;
	[[nodiscard]] bool getCell(int x, int y) const noexcept;
	void setCell(int x, int y, bool cell) noexcept;
	void swap(Field &other) noexcept;

	Field &operator =(Field &&other) noexcept;
private:
	std::vector<std::vector<bool>> field_;
	int height_ = DEFAULT_HEIGHT;
	int width_ = DEFAULT_WIDTH;
};

struct Rules
{
	Rules() noexcept;
	Rules(const Rules &other) = default;
	Rules(Rules &&other) noexcept;
	Rules(std::vector<bool> birth, std::vector<bool> sustain) noexcept;

	Rules &operator =(Rules &&other) noexcept;

	std::vector<bool> birth_;
	std::vector<bool> sustain_;
};

class State
{
public:
	explicit State(Rules rules = Rules(), int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT) noexcept;
	State(State &&other) noexcept;
	State(State &other) = default;

	Field &getCurrent() noexcept;
	[[nodiscard]] int getWidth() const noexcept;
	[[nodiscard]] int getHeight() const noexcept;
	[[nodiscard]] Rules getRules() const;
	void makeNextField();
	State &operator =(State &&other) noexcept;
private:
	Field current_;
	Field next_;
	Rules rules_;
};

#endif //GAME_OF_LIFE_V2_0_ENGINE_H
