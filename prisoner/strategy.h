#ifndef PRISONER_STRATEGY_H
#define PRISONER_STRATEGY_H

#include <iostream>
#include <vector>

constexpr std::size_t COLS = 3;

enum class Choice
{
	COOPERATE = 0,
	DEFECT = 1,
};

struct Result
{
	explicit Result(int cols = COLS);
	std::vector<Choice> choices_;
	std::vector<int> payoffs_;
	std::vector<int> scores_;
};

class Strategy
{
public:
	virtual void make_choice() = 0;
	[[nodiscard]] virtual Choice get_choice() const noexcept = 0;
	virtual void handle_result(const Result &res) = 0;
	virtual ~Strategy() = default;
};

#endif //PRISONER_STRATEGY_H
