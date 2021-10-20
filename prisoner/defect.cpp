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

Choice Defect::make_choice()
{
	std::cout << "made choice to defect" << std::endl;
	return choice;
}

void Defect::show_choice()
{
	std::cout << "show defect" << std::endl;
}
