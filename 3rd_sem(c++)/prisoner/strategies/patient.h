#ifndef PRISONER_PATIENT_H
#define PRISONER_PATIENT_H

#include "../strategy.h"

constexpr std::size_t DEFAULT_VERGE = 5;

class Patient : public Strategy
{
public:
	explicit Patient(std::size_t verge = DEFAULT_VERGE);
	void make_choice() override;
	[[nodiscard]] Choice get_choice() const noexcept override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::COOPERATE;
	std::size_t verge_ = DEFAULT_VERGE;
	std::size_t num_def_ = 0;
};
#endif //PRISONER_PATIENT_H
