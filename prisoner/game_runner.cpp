#include "game_runner.h"

void Detailed_runner::run(CLI &ui)
{
	if (ui.read_msg())
	{
		for (std::size_t i = 0; i < ui.args.steps; ++i)
		{
			strategies.push_back(Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->create_product_by_id(ui.args.strategies[i]));
		}

//		for (std::size_t i = 0; i < ui.args.steps; ++i)
//		{
//			game.step()
//		}
	}
}
