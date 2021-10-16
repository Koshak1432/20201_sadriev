#ifndef PRISONER_FACTORY_H
#define PRISONER_FACTORY_H

#include <map>
#include <functional>

class Strategy;

typedef std::function<Strategy *()> Creator;
typedef std::map<std::string, Creator> assoc_map;

class Strategy_factory
{
public:
	Strategy * create_by_id(const std::string &id);
	bool register_by_id(const std::string &id, Creator creator);
private:
	assoc_map creators_;
};

#endif //PRISONER_FACTORY_H
