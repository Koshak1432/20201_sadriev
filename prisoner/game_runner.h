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

private:
	virtual void print_final(const Result &result) const noexcept = 0; //?????
};

class Fast_runner :public Runner
{
public:
	Fast_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps);
	void run(CLI &ui) override;

private:
	Game game;
	std::vector<std::string> names_;
	std::size_t steps_ = DEFAULT_STEPS;

	void print_final(const Result &result) const noexcept override;
};

class Detailed_runner : public Runner
{
public:
	Detailed_runner(const Matrix &matrix, std::vector<std::string> names);
	void run(CLI &ui) override;

private:
	Game game;
	std::vector<std::string> names_;

	void print_intermediate(const Result &result) const noexcept;
	void print_final(const Result &result) const noexcept override;
};

//consume -- by value, borrow -- by reference
//own games
class Tournament_runner :public Runner
{
public:
	Tournament_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps);
	void run(CLI &ui) override;

private:
	std::vector<std::string> names_;
	std::size_t steps_ = DEFAULT_STEPS;
	Matrix matrix_;

	void print_final(const Result &result) const noexcept override;
};

#endif //PRISONER_GAME_RUNNER_H
