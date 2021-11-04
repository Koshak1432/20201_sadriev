#ifndef PRISONER_RANDOM_H
#define PRISONER_RANDOM_H

#include <random>

#include "strategy.h"

class Random : public Strategy
{
public:
	Random();
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
	std::default_random_engine generator;
};

#endif //PRISONER_RANDOM_H
