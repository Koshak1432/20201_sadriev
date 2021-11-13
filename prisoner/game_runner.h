#ifndef PRISONER_GAME_RUNNER_H
#define PRISONER_GAME_RUNNER_H

#include "strategy_factory.h"
#include "game.h"

class Runner
{
public:
	virtual ~Runner() = default;
	virtual void run() = 0;
};

class Fast_runner :public Runner
{
public:
	Fast_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps);
	void run() override;
private:
	Game game;
	std::vector<std::string> names_;
	std::size_t steps_;
};

class Detailed_runner : public Runner
{
public:
	Detailed_runner(const Matrix &matrix, std::vector<std::string> names);
	void run() override;
private:
	Game game;
	std::vector<std::string> names_;

	void print_intermediate(const Result &result) const noexcept;
};

class Tournament_runner :public Runner
{
public:
	Tournament_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps);
	void run() override;
private:
	std::vector<std::string> names_;
	std::size_t steps_;
	Matrix matrix_;
};

#endif //PRISONER_GAME_RUNNER_H
