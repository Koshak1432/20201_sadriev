#ifndef PRISONER_TIT_FOR_TAT_H
#define PRISONER_TIT_FOR_TAT_H

#include "strategy.h"

class Tit_for_tat : public Strategy
{
public:
	Choice make_choice() override;
	void get_choice() override;

private:
	Choice choice = Choice::COOPERATE;
};

#endif //PRISONER_TIT_FOR_TAT_H
