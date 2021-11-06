#include "defect.h"

#include <iostream>
#include <functional>

#include "factory.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Defect);
	}

	bool b = Strategy_factory::get_instance()->register_creator("defect", create);
}

Choice Defect::get_choice()
{
	return choice_;
}

void Defect::handle_result(const Result &res)
{}

void Defect::make_choice()
{
	choice_ = Choice::DEFECT;
}
