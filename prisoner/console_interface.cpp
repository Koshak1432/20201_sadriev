#include "console_interface.h"
#include <string>

void read(int argc, char **argv, CL_interface::CLI &out)
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
					out.mode_ = CL_interface::Mode::DETAILED;
					continue;
				}
				if ("fast" == after)
				{
					out.mode_ = CL_interface::Mode::FAST;
					continue;
				}
				else if ("tournament" == after)
				{
					out.mode_ = CL_interface::Mode::TOURNAMENT;
					continue;
				}
				else
				{
					throw std::invalid_argument("invalid mode");
				}
			}
			else if ("steps" == before)
			{
				out.steps = std::stoul(after);
			}
			else if ("configs" == before)
			{
				out.config_dir = after;
			}
			else if ("matrix" == before)
			{
				out.matrix_file = after;
			}
			else throw std::invalid_argument("invalid argument in long option");
		}
		else
		{
			out.strategies.push_back(after);
		}
	}
}