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

Choice Change::make_choice()
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

//Choice Tit_for_tat::get_choice()
//{
//	std::cout << ((choice == Choice::DEFECT) ? "get defect" : "get cooperate") << std::endl;
//}
