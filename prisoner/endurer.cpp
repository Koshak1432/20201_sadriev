#include "endurer.h"
#include "factory.h"
#include "io.h"
#include "config_provider.h"
#include <filesystem>

namespace
{
	std::unique_ptr<Strategy> create()
	{
		std::size_t verge = DEFAULT_VERGE;
		std::filesystem::path path(Provider::get_instance()->get_dir());
		std::ifstream stream(path / "endurer.txt");
		if (stream.is_open())
		{
			stream.exceptions(std::ios::badbit | std::ios::failbit);
			std::string input_verge = read_line(stream);
			verge = std::stoul(input_verge); //throw exception invalid arg
		}
		return std::unique_ptr<Strategy>(new Endurer(verge));
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("endurer", create);
}

Choice Endurer::get_choice()
{
	return choice_;
}

void Endurer::handle_result(const Result &res)
{
	for (auto &choice : res.choices_)
	{
		if (Choice::DEFECT == choice)
		{
			++num_def;
		}
	}
	if (num_def > verge_)
	{
		choice_ = Choice::DEFECT;
	}
}

Endurer::Endurer(std::size_t verge) : verge_(verge)
{}
