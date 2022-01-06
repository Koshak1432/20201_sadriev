#ifndef GAME_OF_LIFE_V2_0_ENGINE_H
#define GAME_OF_LIFE_V2_0_ENGINE_H

#include <vector>

#include "Ifield.h"

namespace
{
	constexpr int DEFAULT_WIDTH = 80;
	constexpr int DEFAULT_HEIGHT = 40;
}

class Field : public IField
{
public:
	explicit Field(int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT) noexcept;
	Field(Field &&other) noexcept;
	Field(const Field &other) = default;

	[[nodiscard]] int getHeight() const noexcept override;
	[[nodiscard]] int getWidth() const noexcept override;
	[[nodiscard]] std::size_t countNeighbours(int x, int y) const noexcept;
	[[nodiscard]] bool getCell(int x, int y) const noexcept override;
	void setCell(int x, int y, bool cell) noexcept override;

	void swap(Field &other) noexcept;
	void resize(int newWidth, int newHeight) noexcept;
	void copyToCenterFrom(const Field &other) noexcept;

	Field &operator =(Field &&other) noexcept;
	Field &operator =(const Field &other) = default;

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
	Rules(std::vector<bool> birth, std::vector<bool> survival) noexcept;

	Rules &operator =(Rules &&other) noexcept;
	Rules &operator =(const Rules &other) = default;

	std::vector<bool> birth_;
	std::vector<bool> survival_;
};

class State
{
public:
	explicit State(Rules rules = Rules(), int width = DEFAULT_WIDTH, int height = DEFAULT_HEIGHT) noexcept;
	State(State &&other) noexcept;
	State(State &other) = default;

	Field &getCurrent() noexcept;
	Field &getNext() noexcept;
	[[nodiscard]] int getWidth() const noexcept;
	[[nodiscard]] int getHeight() const noexcept;
	[[nodiscard]] Rules getRules() const noexcept;
	void setRules(Rules rules) noexcept;
	void setBirthRule(int idx, bool checked) noexcept;
	void setSurvivalRule(int idx, bool checked) noexcept;
	void clear() noexcept;
	void makeNextField() noexcept;
	void resize(int newWidth, int newHeight) noexcept;

	State &operator =(State &&other) noexcept;
	State &operator =(const State &other) = default;

private:
	Field current_;
	Field next_;
	Rules rules_;
};

#endif //GAME_OF_LIFE_V2_0_ENGINE_H
