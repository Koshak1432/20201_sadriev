#ifndef PRISONER_GAME_RUNNER_H
#define PRISONER_GAME_RUNNER_H

#include "factory.h"
#include "strategy.h"
#include "console_interface.h"
#include "game.h"

class Runner
{
public:
	virtual ~Runner() = default;
	virtual void run(CLI &ui) = 0;
};

class Detailed_runner : public Runner
{
public:
	void run(CLI &ui) override;

private:
	Game game;
};

class Fast_runner :public Runner
{
public:
	void run(CLI &ui) override;

private:
	Game game;
};

class Tournament_runner :public Runner
{
public:
	void run(CLI &ui) override;

private:
	Game game;
};

#endif //PRISONER_GAME_RUNNER_H
