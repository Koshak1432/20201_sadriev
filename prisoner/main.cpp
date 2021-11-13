#include <exception>

#include "game_runner.h"
#include "io.h"
#include "config_provider.h"
#include "console_interface.h"

int main(int argc, char **argv)
{
	if (argc < 4)
	{
		std::cerr << "gimme strategies!" << std::endl;
		return -1;
	}
	try
	{
		Args args = parse_args(argc, argv);
		Provider::get_instance()->set_dir(args.config_dir);

		std::unique_ptr<Runner> runner = nullptr;

		if (Mode::FAST == args.mode)
		{
			runner = std::make_unique<Fast_runner>(read_matrix(args.matrix_file), args.strategies, args.steps);
		}
		else if (Mode::DETAILED == args.mode)
		{
			runner = std::make_unique<Detailed_runner>(read_matrix(args.matrix_file), args.strategies);
		}
		else
		{
			runner = std::make_unique<Tournament_runner>(read_matrix(args.matrix_file), args.strategies, args.steps);
		}

		runner->run();
	}
	catch(std::exception &e)
	{
		std::cerr << "Catch exception: " << e.what() << std::endl;
		return 11;
	}

	return 0;
}
