#include "random.h"
#include "factory.h"

#include <chrono>

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Random);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("random", create);
}

Choice Random::get_choice()
{
	return choice_;
}

void Random::handle_result(const Result &res)
{
	std::uniform_int_distribution<int> distribution(0, 1);
	int number = distribution(generator);
	if (0 == number)
	{
		choice_ = Choice::COOPERATE;
		return;
	}
	choice_ = Choice::DEFECT;
}

Random::Random() : generator(std::chrono::steady_clock::now().time_since_epoch().count())
{}
