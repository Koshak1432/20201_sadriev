#include "endurer.h"
#include "factory.h"
#include "io.h"

namespace
{
	std::unique_ptr<Strategy> create()
	{
		return std::unique_ptr<Strategy>(new Endurer);
	}

	bool b = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>::get_instance()->register_creator("endurer", create);
}

Choice Endurer::get_choice()
{
	return choice_;
}

void Endurer::handle_result(const Result &res)
{
}

Endurer::Endurer(const std::string &config_dir_path)
{
	std::ifstream stream(config_dir + "endurer.txt");
	stream.exceptions(std::ios::badbit | std::ios::failbit |std::ios::eofbit);
	std::string verge = read_line(stream);

}
