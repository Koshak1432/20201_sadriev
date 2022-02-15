#include "console_interface.h"

#include <iostream>

#include "strategy.h"

Args CLI::parse_args(int argc, char **argv)
{
	Args args;
	std::size_t pos = 0;
	std::string_view before;
	std::string_view argument;
	std::string after;
	for (int i = 1; i < argc; ++i)
	{
		argument = argv[i];
		if ("--" == argument.substr(0, 2))
		{
			pos = argument.find('=');
			if (pos == std::string::npos)
			{
				throw std::invalid_argument("invalid argument");
			}
			after = argument.substr(pos + 1, argument.length() - (pos + 1));
			before = argument.substr(2, pos - 2);
			if (after.empty())
			{
				throw std::invalid_argument("invalid argument after =");
			}
			if ("mode" == before)
			{
				if ("detailed" == after)
				{
					args.mode = Mode::DETAILED;
					continue;
				}
				else if ("fast" == after)
				{
					args.mode = Mode::FAST;
					continue;
				}
				else if ("tournament" == after)
				{
					args.mode = Mode::TOURNAMENT;
					continue;
				}
				else
				{
					throw std::invalid_argument("invalid mode");
				}
			}
			else if ("steps" == before)
			{
				args.steps = std::stoul(after);
			}
			else if ("configs" == before)
			{
				args.config_dir = after;
			}
			else if ("matrix" == before)
			{
				args.matrix_file = after;
			}
			else throw std::invalid_argument("invalid argument in long option");
		}
		else
		{
			args.strategies.emplace_back(std::string(argument));
		}
	}
	if (args.strategies.size() > 3)
	{
		args.mode = Mode::TOURNAMENT;
	}
	return args;
}

bool CLI::read_msg() const noexcept
{
	std::string str;
	std::cin >> str;
	if ("quit" == str)
	{
		return false;
	}
	return true;
}

void CLI::print_after_game(const std::vector<std::string> &names, const Result &result) const noexcept
{
	std::cout << std::string("FINAL SCORES FOR THE TRIPLE") << std::endl;
	for (std::size_t i = 0; i < names.size(); ++i)
	{
		std::cout << "[" + names[i] + ", " << result.scores_[i] << "]" << std::endl;
	}
}

void CLI::print_final(const std::map<std::string, int> &map) const noexcept
{
	std::cout << "------RESULTS FOR ALL STRATEGIES------" << std::endl;
	for (auto &strategy : map)
	{
		std::cout << "[" + strategy.first + ", " << strategy.second << "]" <<std::endl;
	}
}

void CLI::print_intermediate(const std::vector<std::string> &names, const Result &result) const noexcept
{
	std::cout << "--------------" << std::endl;
	for (std::size_t i = 0; i < names.size(); ++i)
	{
		std::string choice = "cooperate";
		if (Choice::DEFECT == result.choices_[i])
		{
			choice = "defect";
		}
		std::cout << "[" + names[i] + ", " + choice + ", " << result.payoffs_[i] << ", " << result.scores_[i] << "]" << std::endl;
	}
	std::cout << "--------------" << std::endl;
}

void CLI::print_help() const noexcept
{
	std::cout << "Mandatory arguments: names of at least 3 strategies" << std::endl;
	std::cout << "Possible options:" << std::endl;
	std::cout << "--mode=[detailed|fast|tournament], --detailed by default for 3 strategies, --tournament for > 3 (OPTIONAL)" << std::endl;
	std::cout << "--steps=<n>, n = 10 by default (OPTIONAL)" << std::endl;
	std::cout << "--configs=<dirname> for full configs directory (OPTIONAL)" << std::endl;
	std::cout << "--matrix=<filename> for full path to game matrix (OPTIONAL)" << std::endl << std::endl;
	std::cout << "In detailed mode press any button + enter for next step" << std::endl << std::endl;
}



