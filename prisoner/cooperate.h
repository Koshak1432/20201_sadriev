#ifndef PRISONER_COOPERATE_H
#define PRISONER_COOPERATE_H

#include "strategy.h"

class Cooperate : public Strategy
{
public:
	Choice make_choice() override;
	void get_choice() override;

private:
	Choice choice = Choice::COOPERATE;
};

#endif //PRISONER_COOPERATE_H
