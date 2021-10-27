#ifndef PRISONER_DEFECT_H
#define PRISONER_DEFECT_H

#include "strategy.h"

class Defect : public Strategy
{
public:
	Choice make_choice() override;
	void get_choice() override;

private:
	Choice choice = Choice::DEFECT;
};

#endif //PRISONER_DEFECT_H
