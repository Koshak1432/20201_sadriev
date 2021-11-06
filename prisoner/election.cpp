#include "election.h"

#include <filesystem>

#include "factory.h"
#include "config_provider.h"
#include "io.h"

namespace
{
	constexpr std::size_t DEFAULT_STRATEGIES_NUM = 3;


	std::unique_ptr<Strategy> create()
	{
		std::filesystem::path path(Provider::get_instance()->get_dir());
		std::ifstream stream(path / "election.txt");
		if (!stream.is_open())
		{
			throw std::invalid_argument("can't open election config file\n");
		}
		stream.exceptions(std::ios::badbit | std::ios::failbit);
		std::vector<std::string> names{};
		std::vector<std::unique_ptr<Strategy>> strategies{};
		strategies.reserve(DEFAULT_STRATEGIES_NUM);
		names.reserve(DEFAULT_STRATEGIES_NUM);
		while (!stream.eof())
		{
			names.push_back(read_line(stream));
		}
		for (auto &name : names)
		{
			strategies.push_back(Strategy_factory::get_instance()->create_product_by_id(name));
		}
		return std::unique_ptr<Strategy>(new Election(strategies));
	}

	bool b = Strategy_factory::get_instance()->register_creator("election", create);
}

Choice Election::get_choice()
{
	return choice_;
}

void Election::handle_result(const Result &res)
{}

void Election::make_choice()
{}

Election::Election(std::vector<std::unique_ptr<Strategy>> strategies) : strategies_(std::move(strategies))
{}
