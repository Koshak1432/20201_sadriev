#ifndef PRISONER_CONSOLE_INTERFACE_H
#define PRISONER_CONSOLE_INTERFACE_H
#include <iostream>
#include <vector>

constexpr std::size_t DEFAULT_STEPS = 10;

enum class Mode
{
	DETAILED = 0,
	FAST,
	TOURNAMENT,
};

struct Args
{
	std::vector<std::string> strategies;
	std::size_t steps = DEFAULT_STEPS;
	Mode mode = Mode::DETAILED;
	std::string config_dir;
	std::string matrix_file;
};

Args parse_args(int argc, char **argv);

class CLI
{
public:
	CLI() = default;
	~CLI() = default;

	bool read_msg();
private:

};

#endif //PRISONER_CONSOLE_INTERFACE_H
