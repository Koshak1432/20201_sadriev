#ifndef PRISONER_CHANGE_H
#define PRISONER_CHANGE_H

#include "strategy.h"

class Change : public Strategy
{
public:
	void make_choice() override;
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice = Choice::COOPERATE;
};

#endif //PRISONER_CHANGE_H
