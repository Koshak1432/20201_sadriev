#ifndef PRISONER_CONFIG_PROVIDER_H
#define PRISONER_CONFIG_PROVIDER_H

#include <string>

class Provider
{
public:
	static Provider *get_instance();
	void set_dir(std::string new_path);
	[[nodiscard]] std::string get_dir() const;
private:
	std::string path_;
};

#endif //PRISONER_CONFIG_PROVIDER_H
