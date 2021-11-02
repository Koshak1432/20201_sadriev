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

static std::size_t binom(std::size_t n, std::size_t k)
{
	if (n < k)
	{
		return 0;
	}
	if (1 == k)
	{
		return n;
	}
	if (0 == k)
	{
		return 1;
	}
	if (k > n / 2)
	{
		k = n - k;
	}
	return binom(n - 1, k) + binom(n - 1, k - 1);
}

Fast_runner::Fast_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps) : game(matrix, make_strategies_from_names(names)), names_(std::move(names)), steps_(steps)
{
	assert(names.size() == 3);
}

Tournament_runner::Tournament_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps) :names_(std::move(names)), steps_(steps), matrix_(matrix)
{
	assert(names.size() >= 3);
}

Detailed_runner::Detailed_runner(const Matrix &matrix, std::vector<std::string> names) :game(matrix, make_strategies_from_names(names)), names_(std::move(names))
{
	assert(names.size() == 3);
}

void Fast_runner::run(CLI &ui)
{
	for (std::size_t i = 0; i < steps_; ++i)
	{
		game.step();
	}
	print_final(game.get_result());
}

void Fast_runner::print_final(const Result &result) const noexcept
{
	std::cout << std::string("FINAL SCORES") << std::endl;
	for (std::size_t i = 0; i < names_.size(); ++i)
	{
		std::cout << names_[i] + "has " << result.scores_[i] << std::endl;
	}
}

void Detailed_runner::run(CLI &ui)
{
	while (ui.read_msg())
	{
		game.step();
		print_intermediate(game.get_result());
	}
	print_final(game.get_result());
}

void Detailed_runner::print_intermediate(const Result &result) const noexcept
{
	std::cout << "--------------" << std::endl;
	for (std::size_t i = 0; i < names_.size(); ++i)
	{
		std::string choice = "cooperate";
		if (Choice::DEFECT == result.choices_[i])
		{
			choice = "defect";
		}
		std::cout << names_[i] + " chose to" + choice + ", got" <<
			result.payoffs_[i] << "points in this round and has " << result.scores_[i] << " in total" << std::endl;
	}
	std::cout << "--------------" << std::endl;
}

void Detailed_runner::print_final(const Result &result) const noexcept
{
	std::cout << std::string("FINAL SCORES") << std::endl;
	for (std::size_t i = 0; i < names_.size(); ++i)
	{
		std::cout << names_[i] + "has " << result.scores_[i] << std::endl;
	}
}

void Tournament_runner::run(CLI &ui)
{
	std::vector<int> total_scores(names_.size()); //!!!!!!!!!!!
	std::vector<bool> bool_vec(names_.size());
	std::fill(bool_vec.end() - COLS, bool_vec.end(), true);
	while (std::next_permutation(bool_vec.begin(), bool_vec.end()))
	{
		std::vector<std::string> names(COLS);
		for(std::size_t i = 0; i < bool_vec.size(); ++i)
		{
			if (bool_vec[i])
			{
				names.push_back(names_[i]);
			}
		}
		Game game(matrix_, make_strategies_from_names(names));
		for (std::size_t i = 0; i < steps_; ++i)
		{
			game.step();
		}
		print_final(game.get_result());
		//add to total score for strategies
	}

	//print games results
	//print final result????
}
