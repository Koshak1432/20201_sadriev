#ifndef PRISONER_CONSOLE_INTERFACE_H
#define PRISONER_CONSOLE_INTERFACE_H
#include <iostream>
#include <vector>

namespace CL_interface
{
	constexpr std::size_t DEFAULT_STEPS = 10;
	enum class Mode
	{
		DETAILED = 0,
		FAST,
		TOURNAMENT,
	};

	struct CLI
	{
		std::vector<std::string> strategies {"","",""};
		std::size_t steps = DEFAULT_STEPS;
		Mode mode = Mode::DETAILED;
		std::string config_dir;
		std::string matrix_file;
	};
}

void read(int argc, char **argv, CL_interface::CLI &out);

#endif //PRISONER_CONSOLE_INTERFACE_H
