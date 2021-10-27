#include "tit_for_tat.h"
#include "factory.h"

namespace
{
	Strategy *create()
	{
		return new Tit_for_tat;
	}

	bool b = Factory<Strategy, std::string, std::function<Strategy *()>>::get_instance()->register_creator("tit_for_tat", create);
}

Choice Tit_for_tat::make_choice()
{
	if (choice == Choice::COOPERATE)
	{
		choice = Choice::DEFECT;
		return Choice::COOPERATE;
	}
	else
	{
		choice = Choice::COOPERATE;
		return Choice::DEFECT;
	}
}

void Tit_for_tat::get_choice()
{
	std::cout << ((choice == Choice::DEFECT) ? "show defect" : "show cooperate") << std::endl;
}
