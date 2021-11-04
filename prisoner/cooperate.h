#ifndef PRISONER_COOPERATE_H
#define PRISONER_COOPERATE_H

#include "strategy.h"

class Cooperate : public Strategy
{
public:
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
};

#endif //PRISONER_COOPERATE_H
