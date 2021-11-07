#include "election.h"

#include <filesystem>

#include "strategy_factory.h"
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
			names.push_back(read_string(stream));
		}
		for (auto &name : names)
		{
			strategies.push_back(Strategy_factory::get_instance()->create_product_by_id(name));
		}
		return std::unique_ptr<Strategy>(new Election(std::move(strategies)));
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
{
	std::size_t num_coop = 0;
	std::size_t num_def = 0;
	for (auto &strategy : strategies_)
	{
		strategy->make_choice();
		(Choice::COOPERATE == strategy->get_choice()) ? ++num_coop : ++num_def;
	}
	(num_coop > num_def) ? choice_ = Choice::COOPERATE : choice_ = Choice::DEFECT;
}

Election::Election(std::vector<std::unique_ptr<Strategy>> strategies) : strategies_(std::move(strategies))
{}
