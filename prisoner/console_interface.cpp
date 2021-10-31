#include "console_interface.h"
#include <string>


void CLI::parse_args(int argc, char **argv)
{
	std::size_t pos = 0;
	std::string_view before;
	std::string_view argument;
	std::string after;
	for (std::size_t i = 1; i < argc; ++i)
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
			if ("mode" == before)
			{
				if ("detailed" == after)
				{
					args.mode = Mode::DETAILED;
					continue;
				}
				if ("fast" == after)
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
			args.strategies.push_back(after);
		}
	}
}

bool CLI::read_msg()
{
	std::string str;
	std::cin >> str;
	if ("quit" == str)
	{
		return false;
	}
	return true;
}

