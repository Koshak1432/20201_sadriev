#ifndef PRISONER_GAME_H
#define PRISONER_GAME_H

#include <vector>
#include <memory>
#include <cassert>

#include "strategy.h"

constexpr std::size_t ROWS = 8;
constexpr std::size_t COLS = 3;

class Matrix
{
public:
	Matrix(std::size_t rows = ROWS, std::size_t cols = COLS);
	~Matrix() = default;
	std::vector<int> get_payoffs(const std::vector<Choice> &choices) const; //get 3 payoffs


	std::vector<int> &operator[] (std::size_t idx);

private:
	std::vector<std::vector<int>> matrix_;

	int choices_to_idx(const std::vector<Choice> &vec) const; //gets row in matrix
};

struct Result
{
	explicit Result(int cols = COLS);
	std::vector<Choice> choices_;
	std::vector<int> payoffs_;
	std::vector<int> scores_;
};

class Game
{
public:
	Game() = default;
	void step(const std::vector<std::unique_ptr<Strategy>> &strategies);
private:
	Matrix matrix_;
	Result res_;
};

#endif //PRISONER_GAME_H
