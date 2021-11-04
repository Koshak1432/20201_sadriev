#include "change.h"
#include "factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Change);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("change", create);
}

Choice Change::get_choice()
{
	return choice_;
}

void Change::handle_result(const Result &res)
{
	if (choice_ == Choice::COOPERATE)
	{
		choice_ = Choice::DEFECT;
		return;
	}
	choice_ = Choice::COOPERATE;
}
