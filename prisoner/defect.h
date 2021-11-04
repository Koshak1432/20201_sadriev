#ifndef PRISONER_DEFECT_H
#define PRISONER_DEFECT_H

#include "strategy.h"

class Defect : public Strategy
{
public:
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::DEFECT;
};

#endif //PRISONER_DEFECT_H
