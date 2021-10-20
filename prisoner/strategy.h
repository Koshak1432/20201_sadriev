#ifndef PRISONER_STRATEGY_H
#define PRISONER_STRATEGY_H

enum class Choice
{
	DEFECT = 0,
	COOPERATE,
};

class Strategy
{
public:
	Strategy();
	~Strategy();

	virtual Choice make_choice();
	virtual void show_choice();
};

#endif //PRISONER_STRATEGY_H
