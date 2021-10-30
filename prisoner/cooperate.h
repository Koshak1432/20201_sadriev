#ifndef PRISONER_COOPERATE_H
#define PRISONER_COOPERATE_H

#include "strategy.h"

class Cooperate : public Strategy
{
public:
	void make_choice() override;
	Choice get_choice() override;

private:
	Choice choice = Choice::COOPERATE;
};

#endif //PRISONER_COOPERATE_H
