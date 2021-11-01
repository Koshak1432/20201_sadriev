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
	Matrix();
	explicit Matrix(std::size_t rows = ROWS, std::size_t cols = COLS);
	~Matrix() = default;
	std::vector<int> get_payoffs(const std::vector<Choice> &choices) const; //get 3 payoffs
	std::vector<int> &operator[] (std::size_t idx);
private:
	std::vector<std::vector<int>> matrix_;
};

struct Result //как принтить с именами, если имена уже выкинуты?????
{
	explicit Result(int cols = COLS);
	std::vector<Choice> choices_;
	std::vector<int> payoffs_;
	std::vector<int> scores_;

	void print();
};

//matrix from runner, to runner from main
//strategies from runner, to runner from cli by making from names
class Game
{
public:
	Game(const Matrix &matrix, std::vector<std::unique_ptr<Strategy>> strategies);
	void step();
private:
	Matrix matrix_;
	std::vector<std::unique_ptr<Strategy>> strategies_;
	Result res_;
};

#endif //PRISONER_GAME_H
