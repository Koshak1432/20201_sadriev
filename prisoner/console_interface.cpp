#include "console_interface.h"
#include <string>

Args parse_args(int argc, char **argv)
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
				args.steps = std::stoul(after); //check
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
	return args;
}


