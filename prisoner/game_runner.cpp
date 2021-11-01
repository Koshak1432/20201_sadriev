#include "game_runner.h"

static std::vector<std::unique_ptr<Strategy>> make_strategies_from_names(const std::vector<std::string> &names)
{
	std::vector<std::unique_ptr<Strategy>> strategies(names.size());
	for (auto &name : names)
	{
		strategies.push_back(Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->create_product_by_id(name));
	}
	return strategies;
}

Fast_runner::Fast_runner(const Matrix &matrix, const std::vector<std::string> &names, std::size_t steps) : game(matrix, make_strategies_from_names(names)), steps_(steps)
{
	assert(names.size() == 3);
}

Tournament_runner::Tournament_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps) :names_(std::move(names)), steps_(steps)
{
	assert(names.size() >= 3);
}

Detailed_runner::Detailed_runner(const Matrix &matrix, const std::vector<std::string> &names) :game(matrix, make_strategies_from_names(names))
{
	assert(names.size() == 3);
}

void Fast_runner::run(CLI &ui)
{
	for (std::size_t i = 0; i < steps_; ++i)
	{
		game.step();
	}
	//print result
}

void Detailed_runner::run(CLI &ui)
{
	while (ui.read_msg())
	{
		game.step();
		//print intermediate result
	}
	//print final result
}

void Tournament_runner::run(CLI &ui)
{
	for (std::size_t i = 0; i < steps_; ++i)
	{
		//form binom{names.size()}{3} vector strategies
		//make games from them

	}
	//print games results
	//print final result????
}
