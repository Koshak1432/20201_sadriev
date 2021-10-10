#ifndef FLATMAP_FLATMAP_H
#define FLATMAP_FLATMAP_H
#include "Array.h"
#include <iostream>
#include <stdexcept>

//the associative <Key, Value> pair container
//size is equal to the size of the array field
template<class Key, class Value>
class FlatMap
{
public:
	explicit FlatMap(std::size_t size = 1); //takes flatmap size as parameter
	FlatMap(const FlatMap<Key, Value> &other); //copy constructor
	FlatMap(FlatMap<Key, Value> &&other) noexcept; //move constructor
	~FlatMap() = default;

	void swap(FlatMap<Key, Value> &other); //swaps fields of two flatmaps

	FlatMap<Key, Value> &operator =(const FlatMap<Key, Value> &other); //assigns left operand field to the other fields
	FlatMap<Key, Value> &operator =(FlatMap<Key, Value> &&other) noexcept; //move assignment without copying fields

	bool erase(const Key &key) noexcept; //erases element by the key, returns false if a flatmap doesn't contain an element with such key, else returns true
	void clear() noexcept; //applies erase method for every element in fields of the object
	bool insert(const Key &key, const Value &value); //returns false if a flatmap contains an element with such key, else returns true
	bool contains(const Key &key) const noexcept; // returns true if a flatmap contains an element with such key, else returns false

	Value &operator [](const Key &key) noexcept; //returns a reference to the Value by the key, inserts an element with default value
												// if a flatmap doesn't contain such an element

	Value &at(const Key &key); // returns a reference to the Value by the key, throw out_of_range if a ftalmap doesn't contain an element with such key
	const Value &at(const Key &key) const;

	std::size_t size() const noexcept; //gets the number of elements in a flatmap
	bool empty() const noexcept; //returns true if the flatmap is empty

	//returns true if the fields of the first flatmap are equal to the fields of the other
	friend bool operator ==(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second) noexcept
	{
		if (first.key_arr_.get_size() != second.key_arr_.get_size())
		{
			return false;
		}
		for (std::size_t i = 0; i < first.key_arr_.get_size(); ++i)
		{
			if (first.key_arr_[i] != second.key_arr_[i] || first.val_arr_[i] != second.val_arr_[i])
			{
				return false;
			}
		}
		return true;
	}

	//returns true if the fields of the first flatmap aren't equal to the fields of the other
	friend bool operator !=(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second) noexcept
	{
		return !operator ==(first, second);
	}

	//void print_flatmap();

private:
	Array<Key> key_arr_; //keys array
	Array<Value> val_arr_; //values array
	std::size_t bin_search(const Key &key) const; //lover_bound method
};

template<class Key, class Value>
bool FlatMap<Key, Value>::empty() const noexcept
{
	return key_arr_.get_size() == 0;
}

template<class Key, class Value>
std::size_t FlatMap<Key, Value>::size() const noexcept
{
	return key_arr_.get_size();
}

template<class Key, class Value>
std::size_t FlatMap<Key, Value>::bin_search(const Key &key) const
{
	if (0 == key_arr_.get_size())
	{
		return 0;
	}
	std::size_t count = key_arr_.get_size() - 1;
	std::size_t first = 0;
	std::size_t idx = 0;
	std::size_t step = 0;
	while (count > 0)
	{
		idx = first;
		step = count / 2;
		idx += step;
		if (key_arr_[idx] < key)
		{
			first = ++idx;
			count -= step + 1;
		}
		else
		{
			count = step;
		}
	}
	return first;
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(std::size_t size) : key_arr_(size), val_arr_(size)
{
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(FlatMap<Key, Value> &&other)  noexcept
			: val_arr_(std::move(other.val_arr_)), key_arr_(std::move(other.key_arr_))
{
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(const FlatMap<Key, Value> &other) : key_arr_(other.key_arr_), val_arr_(other.val_arr_)
{
}

template<class Key, class Value>
FlatMap<Key, Value> &FlatMap<Key, Value>::operator =(const FlatMap<Key, Value> &other)
{
	if (&other != this)
	{
		key_arr_ = other.key_arr_;
		val_arr_ = other.val_arr_;
	}
	return *this;
}

template<class Key, class Value>
FlatMap<Key, Value> &FlatMap<Key, Value>::operator =(FlatMap<Key, Value> &&other) noexcept
  {
	if (&other != this)
	{
		key_arr_ = std::move(other.key_arr_);
		val_arr_ = std::move(other.val_arr_);
	}
	return *this;
}

template<class Key, class Value>
void FlatMap<Key, Value>::clear() noexcept
{
	key_arr_.clear();
	val_arr_.clear();
}

template<class Key, class Value>
bool FlatMap<Key, Value>::contains(const Key &key) const noexcept
{
	std::size_t idx = bin_search(key);
	if (idx == key_arr_.get_size())
	{
		return false;
	}
	return key == key_arr_[idx];
}

template<class Key, class Value>
bool FlatMap<Key, Value>::erase(const Key &key) noexcept
{
	std::size_t idx = bin_search(key);
	if (key == key_arr_[idx])
	{
		key_arr_.erase(idx);
		val_arr_.erase(idx);
		return true;
	}
	return false;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::insert(const Key &key, const Value &value)
{
	std::size_t idx = bin_search(key);
	if (idx != key_arr_.get_size())
	{
		if (key_arr_[idx] == key)
		{
			return false;
		}
	}
	key_arr_.insert(idx, key);
	val_arr_.insert(idx, value);
	return true;
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::at(const Key &key)
{
	std::size_t idx = bin_search(key);
	if (key != key_arr_[idx])
	{
		throw std::out_of_range("there is no such key in the flatmap");
	}
	return val_arr_[idx];
}

template<class Key, class Value>
const Value &FlatMap<Key, Value>::at(const Key &key) const
{
	std::size_t idx = bin_search(key);
	if (key != key_arr_[idx])
	{
		throw std::out_of_range("there is no such key in the flatmap");
	}
	return val_arr_[idx];
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::operator [](const Key &key) noexcept
{
	std::size_t idx = bin_search(key);
	assert(idx < key_arr_.get_size() && idx >= 0);
	if (key != key_arr_[idx])
	{
		insert(key, Value());
	}
	return val_arr_[idx];
}

template<class Key, class Value>
void FlatMap<Key, Value>::swap(FlatMap<Key, Value> &other)
{
	Array<Key> tmp_key = key_arr_;
	Array<Value> tmp_val = val_arr_;
	key_arr_ = other.key_arr_;
	val_arr_ = other.val_arr_;
	other.key_arr_ = tmp_key;
	other.val_arr_ = tmp_val;
}


//template<class Key, class Value>
//void FlatMap<Key, Value>::print_flatmap()
//{
//	cout << "___________________________________" << endl;
//	for (std::size_t i = 0; i < key_arr_.get_size(); ++i)
//	{
//		cout << "idx " << i <<  " | key " << key_arr_[i] << " | value " << val_arr_[i] << endl;
//	}
//	cout << "___________________________________" << endl;
//}

#endif //FLATMAP_FLATMAP_H
