#ifndef PRISONER_CHANGE_H
#define PRISONER_CHANGE_H

#include "strategy.h"

class Change : public Strategy
{
public:
	Choice make_choice() override;
	//Choice get_choice() override;

private:
	Choice choice = Choice::COOPERATE;
};

#endif //PRISONER_CHANGE_H
