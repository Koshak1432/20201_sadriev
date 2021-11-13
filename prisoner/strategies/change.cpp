#include "change.h"
#include "../strategy_factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::make_unique<Change>();
	}

	bool b = Strategy_factory::get_instance()->register_creator("change", create);
}

Choice Change::get_choice() const noexcept
{
	return choice_;
}

void Change::handle_result(const Result &res)
{}

void Change::make_choice()
{
	if (choice_ == Choice::COOPERATE)
	{
		choice_ = Choice::DEFECT;
		return;
	}
	choice_ = Choice::COOPERATE;
}
