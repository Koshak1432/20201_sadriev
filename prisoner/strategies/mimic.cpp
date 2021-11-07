#include "mimic.h"
#include "../strategy_factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Mimic);
	}

	bool b = Strategy_factory::get_instance()->register_creator("mimic", create);
}

Choice Mimic::get_choice()
{
	return choice_;
}

void Mimic::handle_result(const Result &res)
{
	int max = 0;
	for (std::size_t i = 0; i < res.scores_.size(); ++i)
	{
		if (res.scores_[i] > max)
		{
			max = res.scores_[i];
			choice_ = res.choices_[i];
		}
	}
}

void Mimic::make_choice()
{}
