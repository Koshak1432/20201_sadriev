#include "patient.h"

#include <filesystem>

#include "../strategy_factory.h"
#include "../io.h"
#include "../config_provider.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		std::size_t verge = DEFAULT_VERGE;
		std::filesystem::path path(Provider::get_instance()->get_dir());
		std::ifstream stream(path / "patient.txt");
		if (stream.is_open())
		{
			stream.exceptions(std::ios::badbit | std::ios::failbit);
			verge = read_size_t(stream); //throw exception invalid arg
		}
		return std::make_unique<Patient>(verge);
	}

	bool b = Strategy_factory::get_instance()->register_creator("patient", create);
}

Choice Patient::get_choice() const noexcept
{
	return choice_;
}

void Patient::handle_result(const Result &res)
{
	for (auto &choice : res.choices_)
	{
		if (Choice::DEFECT == choice)
		{
			++num_def_;
		}
	}
}

Patient::Patient(std::size_t verge) : verge_(verge)
{}

void Patient::make_choice()
{
	(num_def_ > verge_) ? choice_ = Choice::DEFECT : choice_ = Choice::COOPERATE;
}
