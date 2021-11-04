#ifndef PRISONER_CHANGE_H
#define PRISONER_CHANGE_H

#include "strategy.h"

class Change : public Strategy
{
public:
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
};

#endif //PRISONER_CHANGE_H
