#include "console_interface.h"
#include "factory.h"
#include <functional>
#include <vector>
#include <memory>
#include "strategy.h"


Factory<Strategy, std::string, std::function<Strategy *()>> factory;


int main(int argc, char **argv)
{
	if (argc < 4)
	{
		std::cerr << "gimme 3 strategies" << std::endl;
		return -1;
	}
	std::vector<std::unique_ptr<Strategy>> strategies;

	CL_interface::CLI CL_arguments;
	read(argc, argv, CL_arguments);

	return 0;
}
