#ifndef PRISONER_FACTORY_H
#define PRISONER_FACTORY_H

#include <iostream>
#include <map>
#include <functional>
#include <memory>

//struct Error_handling
//{
//	static void not_found()
//	{
//		throw std::invalid_argument("not found");
//	}
//};

template<class Product, class Id_type, class Creator>
class Factory
{
public:
	//singleton
	static Factory *get_instance();

	std::unique_ptr<Product> create_product_by_id(const Id_type &id);
	bool register_creator(const Id_type &id, Creator creator);
private:
	std::map<Id_type, Creator> creators_;
};

template<class Product, class Id_type, class Creator>
Factory<Product, Id_type, Creator> *Factory<Product, Id_type, Creator>::get_instance()
{
	static Factory f;
	return &f;
}

template<class Product, class Id_type, class Creator>
std::unique_ptr<Product> Factory<Product, Id_type, Creator>::create_product_by_id(const Id_type &id)
{
	auto it = creators_.find(id);
	if (it == creators_.end())
	{
		throw std::invalid_argument("invalid");
	}
	return it->second();
}

template<class Product, class Id_type, class Creator>
bool Factory<Product, Id_type, Creator>::register_creator(const Id_type &id, Creator creator)
{
	creators_.at(id) = creator;
	return true;
}

#endif //PRISONER_FACTORY_H
