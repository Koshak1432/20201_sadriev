#include "majority.h"
#include "factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Majority);
	}

	bool b = Strategy_factory::get_instance()->register_creator("majority", create);
}

Choice Majority::get_choice()
{
	return choice_;
}

void Majority::handle_result(const Result &res)
{
	for (auto &choice : res.choices_)
	{
		if (Choice::COOPERATE == choice)
		{
			++num_coop;
			continue;
		}
		++num_def;
	}
}

void Majority::make_choice()
{
	(num_coop > num_def) ? choice_ = Choice::COOPERATE : choice_ = Choice::DEFECT;
}
