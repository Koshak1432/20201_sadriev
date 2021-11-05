#ifndef PRISONER_ENDURER_H
#define PRISONER_ENDURER_H

#include "strategy.h"

class Endurer : public Strategy
{
public:
	Endurer(std::size_t verge);
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
	std::size_t verge_ = 0;
};
#endif //PRISONER_ENDURER_H
