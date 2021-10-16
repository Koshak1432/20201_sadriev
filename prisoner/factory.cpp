#include "factory.h"

bool Strategy_factory::register_by_id(const std::string &id, Creator creator)
{
	return creators_.insert(assoc_map::value_type(id, creator)).second;
}

Strategy *Strategy_factory::create_by_id(const std::string &id)
{
	assoc_map::const_iterator i = creators_.find(id);
	if (i == creators_.end())
	{
		//todo errors handling
	}
	return (i->second)();
}
