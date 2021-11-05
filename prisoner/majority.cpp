#include "majority.h"
#include "factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Majority);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("majority", create);
}

Choice Majority::get_choice()
{
	return choice_;
}

void Majority::handle_result(const Result &res) //todo ??????????????????????
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
	(num_coop > num_def) ? choice_ = Choice::COOPERATE : choice_ = Choice::DEFECT;
}