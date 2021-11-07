#ifndef PRISONER_MAJORITY_H
#define PRISONER_MAJORITY_H

#include "../strategy.h"

class Majority : public Strategy
{
public:
	void make_choice() override;
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
	std::size_t num_coop = 0;
	std::size_t num_def = 0;
};

#endif //PRISONER_MAJORITY_H
