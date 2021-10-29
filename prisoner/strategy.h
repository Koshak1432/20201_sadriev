#ifndef PRISONER_STRATEGY_H
#define PRISONER_STRATEGY_H

#include <iostream>

enum class Choice
{
	COOPERATE = 0,
	DEFECT = 1,
};

class Strategy
{
public:
	virtual Choice make_choice() = 0;
	virtual Choice get_choice() = 0;

	virtual ~Strategy()
	{
		std::cout << "strategy dtor" << std::endl;
	}
};

#endif //PRISONER_STRATEGY_H
