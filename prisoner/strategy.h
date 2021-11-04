#ifndef PRISONER_STRATEGY_H
#define PRISONER_STRATEGY_H

#include <iostream>

struct Result;

enum class Choice
{
	COOPERATE = 0,
	DEFECT = 1,
};

class Strategy
{
public:
	virtual Choice get_choice() = 0;
	virtual void handle_result(const Result &res) = 0;
	virtual ~Strategy() = default;
};

#endif //PRISONER_STRATEGY_H
