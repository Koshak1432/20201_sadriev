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

//Choice Defect::get_choice()
//{
//	std::cout << "get defect" << std::endl;
//}
