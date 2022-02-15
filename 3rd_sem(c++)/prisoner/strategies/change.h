#ifndef PRISONER_CHANGE_H
#define PRISONER_CHANGE_H

#include "../strategy.h"

class Change : public Strategy
{
public:
	void make_choice() override;
	[[nodiscard]] Choice get_choice() const noexcept override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
};

#endif //PRISONER_CHANGE_H
