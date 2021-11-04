#include "cooperate.h"
#include "factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Cooperate);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("cooperate", create);
}

Choice Cooperate::get_choice()
{
	return choice_;
}

void Cooperate::handle_result(const Result &res)
{
	choice_ = Choice::COOPERATE;
}
