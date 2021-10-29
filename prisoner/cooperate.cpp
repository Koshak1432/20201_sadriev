#include "cooperate.h"
#include "factory.h"

namespace
{
	Strategy *create()
	{
		return new Cooperate;
	}

	bool b = Factory<Strategy, std::string, std::function<Strategy *()>>::get_instance()->register_creator("cooperate", create);
}

Choice Cooperate::make_choice()
{
	return choice;
}

//Choice Cooperate::get_choice()
//{
//	std::cout << "show cooperate" << std::endl;
//}
