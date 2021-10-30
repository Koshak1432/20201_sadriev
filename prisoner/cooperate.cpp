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

void Cooperate::make_choice()
{
	choice = Choice::COOPERATE;
}

Choice Cooperate::get_choice()
{
	return choice;
}
