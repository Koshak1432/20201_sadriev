#include "game_runner.h"
#include "io.h"
#include "config_provider.h"

int main(int argc, char **argv)
{
	if (argc < 4)
	{
		std::cerr << "gimme strategies!\n" << std::endl;
		return -1;
	}
	CLI ui;
	Args args = parse_args(argc, argv);
	Provider::get_instance()->set_dir(args.config_dir);

	std::unique_ptr<Runner> runner = nullptr;

	if (Mode::FAST == args.mode)
	{
		runner = std::unique_ptr<Runner>(new Fast_runner(read_matrix(args.matrix_file), args.strategies, args.steps));
	}
	else if (Mode::DETAILED == args.mode)
	{
		runner = std::unique_ptr<Runner>(new Detailed_runner(read_matrix(args.matrix_file), args.strategies));
	}
	else
	{
		runner = std::unique_ptr<Runner>(new Tournament_runner(read_matrix(args.matrix_file), args.strategies, args.steps));
	}

	runner->run(ui);
	return 0;
}
