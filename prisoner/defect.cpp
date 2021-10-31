#include "defect.h"
#include "factory.h"
#include <iostream>
#include <functional>

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Defect);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("defect", create);
}

void Defect::make_choice()
{
	choice = Choice::DEFECT;
}

Choice Defect::get_choice()
{
	return choice;
}
