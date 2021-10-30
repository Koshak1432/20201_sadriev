#include "defect.h"
#include "factory.h"
#include <iostream>
#include <functional>

namespace
{
	Strategy *create()
	{
		return new Defect;
	}

	bool b = Factory<Strategy, std::string, std::function<Strategy *()>>::get_instance()->register_creator("defect", create);
}

void Defect::make_choice()
{
	choice = Choice::DEFECT;
}

Choice Defect::get_choice()
{
	return choice;
}
