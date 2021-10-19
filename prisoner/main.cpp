#include "console_interface.h"

enum class Choice
{
	DEFECT = 0,
	COOPERATE,
};

class Strategy
{
public:
	Strategy();
	~Strategy();

	virtual Choice make_choice();
	virtual void show_choice();
};

class Always_defect : public Strategy
{
public:
	Always_defect();
	~Always_defect();

	Choice make_choice() override;
	void show_choice() override;

};

Choice Always_defect::make_choice()
{
	return Choice::DEFECT;
}

int main(int argc, char **argv)
{
	CL_interface::CLI CL_arguments;
	read(argc, argv, CL_arguments);

	return 0;
}
