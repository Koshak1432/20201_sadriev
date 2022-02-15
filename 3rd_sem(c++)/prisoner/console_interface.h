#ifndef PRISONER_CONSOLE_INTERFACE_H
#define PRISONER_CONSOLE_INTERFACE_H

#include <string>
#include <vector>
#include <map>

constexpr std::size_t DEFAULT_STEPS = 10;

class Result;

enum class Mode
{
	DETAILED = 0,
	FAST,
	TOURNAMENT,
};

struct Args
{
	std::vector<std::string> strategies{};
	std::size_t steps = DEFAULT_STEPS;
	Mode mode = Mode::DETAILED;
	std::string config_dir{};
	std::string matrix_file{};
};

class CLI
{
public:
	Args parse_args(int argc, char **argv);
	void print_help() const noexcept;
	void print_after_game(const std::vector<std::string> &names, const Result &result) const noexcept;
	void print_final(const std::map<std::string, int> &map) const noexcept;
	[[nodiscard]] bool read_msg() const noexcept;
	void print_intermediate(const std::vector<std::string> &names, const Result &result) const noexcept;
};

#endif //PRISONER_CONSOLE_INTERFACE_H
