#ifndef GAME_OF_LIFE_V2_0_ENGINE_H
#define GAME_OF_LIFE_V2_0_ENGINE_H

#include <vector>

constexpr int DEFAULT_WIDTH = 80;
constexpr int DEFAULT_HEIGHT = 40;

class Field
{
public:
	explicit Field(int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT); //check > 0 ??????

	[[nodiscard]] int getHeight() const noexcept;
	[[nodiscard]] int getWidth() const noexcept;
	[[nodiscard]] std::size_t countNeighbours(int x, int y) const noexcept;
	[[nodiscard]] bool getCell(int x, int y) const noexcept;
	void setCell(int x, int y, bool cell) noexcept;
	void swap(Field &other) noexcept;
private:
	std::vector<std::vector<bool>> field_;
	int height_ = DEFAULT_HEIGHT;
	int width_ = DEFAULT_WIDTH;
};

class State
{
public:
	explicit State(int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT);
	Field &getField() noexcept;
	void makeNextField();
private:
	Field current_;
	Field next_;
};

#endif //GAME_OF_LIFE_V2_0_ENGINE_H
