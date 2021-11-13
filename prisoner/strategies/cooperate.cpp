#include "cooperate.h"
#include "../strategy_factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::make_unique<Cooperate>();
	}

	bool b = Strategy_factory::get_instance()->register_creator("cooperate", create);
}

Choice Cooperate::get_choice() const noexcept
{
	return choice_;
}

void Cooperate::handle_result(const Result &res)
{}

void Cooperate::make_choice()
{
	choice_ = Choice::COOPERATE;
}
