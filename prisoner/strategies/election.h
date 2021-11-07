#ifndef PRISONER_ELECTION_H
#define PRISONER_ELECTION_H

#include "../strategy.h"
#include <memory>

class Election : public Strategy
{
public:
	explicit Election(std::vector<std::unique_ptr<Strategy>> strategies);
	void make_choice() override;
	Choice get_choice() override;
	void handle_result(const Result &res) override;
private:
	Choice choice_ = Choice::DEFECT;
	std::vector<std::unique_ptr<Strategy>> strategies_;
};

#endif //PRISONER_ELECTION_H
