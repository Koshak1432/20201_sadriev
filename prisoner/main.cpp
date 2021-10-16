#include <iostream>
#include <vector>

enum class Choice
{
	DEFECT = 0,
	COOPERATE,
};

namespace CL_interface
{
	constexpr std::size_t DEFAULT_STEPS = 10;
	enum class Mode
	{
		DETAILED = 0,
		FAST,
		TOURNAMENT,
	};

	struct CLI
	{
		std::vector<std::string> strategies {"","",""};
		std::size_t steps = DEFAULT_STEPS;
		Mode mode_ = Mode::DETAILED;
		std::string config_dir;
		std::string matrix_file;
	};
}

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

int main(int argc, char * argv[])
{
	CL_interface::CLI CL_arguments;
	//read CL(CL_interface::CLI &out);

	return 0;
}
