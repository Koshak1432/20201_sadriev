#include "random.h"

#include <chrono>

#include "../strategy_factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Random);
	}

	bool b = Strategy_factory::get_instance()->register_creator("random", create);
}

Choice Random::get_choice() const noexcept
{
	return choice_;
}

void Random::handle_result(const Result &res)
{}

Random::Random() : generator(std::chrono::steady_clock::now().time_since_epoch().count())
{}

void Random::make_choice()
{
	std::uniform_int_distribution<int> distribution(0, 1);
	int number = distribution(generator);
	(0 == number) ? choice_ = Choice::COOPERATE : choice_ = Choice::DEFECT;
}
