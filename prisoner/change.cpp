#include "change.h"
#include "factory.h"

namespace
{
	Strategy *create()
	{
		return new Change;
	}

	bool b = Factory<Strategy, std::string, std::function<Strategy *()>>::get_instance()->register_creator("change", create);
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
