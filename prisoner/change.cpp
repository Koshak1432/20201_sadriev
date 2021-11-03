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

void Change::make_choice()
{
	if (choice == Choice::COOPERATE)
	{
		choice = Choice::DEFECT;
	}
	else
	{
		choice = Choice::COOPERATE;
	}
}

Choice Change::get_choice()
{
	return choice;
}

void Change::handle_result(const Result &res)
{}
