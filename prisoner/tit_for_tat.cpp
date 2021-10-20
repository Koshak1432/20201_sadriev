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