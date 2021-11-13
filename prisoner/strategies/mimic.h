#ifndef PRISONER_MIMIC_H
#define PRISONER_MIMIC_H

#include "../strategy.h"

class Mimic : public Strategy
{
public:
	void make_choice() override;
	[[nodiscard]] Choice get_choice() const noexcept override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
};

#endif //PRISONER_MIMIC_H
