#ifndef PRISONER_STRATEGY_H
#define PRISONER_STRATEGY_H

#include <iostream>

enum class Choice
{
	DEFECT = 0,
	COOPERATE,
};

class Strategy
{
public:
	virtual Choice make_choice() = 0;
	virtual void show_choice() = 0;

	virtual ~Strategy()
	{
		std::cout << "strategy dtor" << std::endl;
	}
};

#endif //PRISONER_STRATEGY_H
