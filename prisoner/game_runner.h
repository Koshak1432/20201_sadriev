#ifndef PRISONER_GAME_RUNNER_H
#define PRISONER_GAME_RUNNER_H

#include "factory.h"
#include "strategy.h"
#include "console_interface.h"

class Runner
{
public:
	virtual ~Runner() = default;
	virtual void run(CL_interface::CLI &ui) = 0;
};

class Detailed_runner : public Runner
{
	void run(CL_interface::CLI &ui) override;
};

class Fast_runner :public Runner
{
	void run(CL_interface::CLI &ui) override;
};

class Tournament_runner :public Runner
{
	void run(CL_interface::CLI &ui) override;
};

#endif //PRISONER_GAME_RUNNER_H
