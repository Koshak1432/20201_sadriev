#include "config_provider.h"

void Provider::set_dir(std::string new_path) noexcept
{
	path_ = std::move(new_path);
}

Provider *Provider::get_instance()
{
	static Provider provider;
	return &provider;
}

std::string Provider::get_dir() const noexcept
{
	return path_;
}

