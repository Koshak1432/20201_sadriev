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
					args::mode = Mode::DETAILED;
					continue;
				}
				if ("fast" == after)
				{
					mode = Mode::FAST;
					continue;
				}
				else if ("tournament" == after)
				{
					mode = Mode::TOURNAMENT;
					continue;
				}
				else
				{
					throw std::invalid_argument("invalid mode");
				}
			}
			else if ("steps" == before)
			{
				steps = std::stoul(after);
			}
			else if ("configs" == before)
			{
				config_dir = after;
			}
			else if ("matrix" == before)
			{
				matrix_file = after;
			}
			else throw std::invalid_argument("invalid argument in long option");
		}
		else
		{
			strategies.push_back(after);
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

