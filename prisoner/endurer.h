#ifndef PRISONER_ENDURER_H
#define PRISONER_ENDURER_H

#include "strategy.h"

constexpr std::size_t DEFAULT_VERGE = 10;

class Endurer : public Strategy
{
public:
	explicit Endurer(std::size_t verge = DEFAULT_VERGE);
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
	std::size_t verge_ = DEFAULT_VERGE;
	std::size_t num_def = 0;
};
#endif //PRISONER_ENDURER_H
