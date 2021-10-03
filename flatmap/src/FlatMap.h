#ifndef FLATMAP_FLATMAP_H
#define FLATMAP_FLATMAP_H
#include "Array.h"
#include <cstring>
#include <iostream>
#include <stdexcept>
#include <utility>

template<class Key, class Value>
class FlatMap
{
public:
	explicit FlatMap(size_t size = 1);
	~FlatMap() = default;

	FlatMap(const FlatMap<Key, Value> &other);
	FlatMap(FlatMap<Key, Value> &&other) noexcept;

	void swap(FlatMap<Key, Value> &other);

	FlatMap<Key, Value> &operator =(const FlatMap<Key, Value> &other);
	FlatMap<Key, Value> &operator =(FlatMap<Key, Value> &&other) noexcept;

	void clear();
	bool erase(const Key &key);
	bool insert(const Key &key, const Value &value);
	bool contains(const Key &key) const;

	Value &operator [](const Key &key);

	Value &at(const Key &key);
	const Value &at(const Key &key) const;

	size_t size() const; //get number of elements in the container
	bool empty() const;

	friend bool operator ==(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second)
	{
		if (first.key_arr_.get_size() != second.key_arr_.get_size())
		{
			return false;
		}
		for (size_t i = 0; i < first.key_arr_.get_size(); ++i)
		{
			if (first.key_arr_[i] != second.key_arr_[i] || first.val_arr_[i] != second.val_arr_[i])
			{
				return false;
			}
		}
		return true;
	}
	friend bool operator !=(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second)
	{
		return !operator ==(first, second);
	}

	void print_flatmap();

private:
	Array<Key> key_arr_;
	Array<Value> val_arr_;
	size_t bin_search(const Key &key) const;
};

template<class Key, class Value>
bool FlatMap<Key, Value>::empty() const
{
	return key_arr_.get_size() == 0;
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::size() const
{
	return key_arr_.get_size();
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::bin_search(const Key &key) const
{
	if (0 == key_arr_.get_size())
	{
		return 0;
	}
	size_t count = key_arr_.get_size() - 1;
	size_t first = 0;
	size_t idx = 0;
	size_t step = 0;
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
FlatMap<Key, Value>::FlatMap(size_t size) : key_arr_(size), val_arr_(size)
{
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(FlatMap<Key, Value> &&other)  noexcept
			: val_arr_(std::move(other.val_arr_)), key_arr_(std::move(other.key_arr_))
{
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(const FlatMap<Key, Value> &other) : key_arr_{other.key_arr_}, val_arr_{other.val_arr_}
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
void FlatMap<Key, Value>::clear() //erase for every key
{
	for (long long i = key_arr_.get_size() - 1; i >= 0; --i)
	{
		key_arr_.erase(i);
		val_arr_.erase(i);
	}
}

template<class Key, class Value>
bool FlatMap<Key, Value>::contains(const Key &key) const
{
	size_t idx = bin_search(key);
	return key == key_arr_[idx];
}

template<class Key, class Value>
bool FlatMap<Key, Value>::erase(const Key &key)
{
	size_t idx = bin_search(key);
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
	size_t idx = bin_search(key);
	if (key_arr_[idx] == key)
	{
		return false;
	}
	key_arr_.insert(idx, key);
	val_arr_.insert(idx, value);
	return true;
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::at(const Key &key)
{
	size_t idx = bin_search(key);
	if (key != key_arr_[idx])
	{
		throw std::domain_error("there is no such key in the flatmap");
	}
	return val_arr_[idx];
}

template<class Key, class Value>
const Value &FlatMap<Key, Value>::at(const Key &key) const
{
	size_t idx = bin_search(key);
	if (key != key_arr_[idx])
	{
		throw std::domain_error("there is no such key in the flatmap");
	}
	return val_arr_[idx];
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::operator [](const Key &key)
{
	size_t idx = bin_search(key);
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

template<class Key, class Value>
void FlatMap<Key, Value>::print_flatmap()
{
	cout << "___________________________________" << endl;
	for (size_t i = 0; i < key_arr_.get_size(); ++i)
	{
		cout << "idx " << i <<  " | key " << key_arr_[i] << " | value " << val_arr_[i] << endl;
	}
	cout << "___________________________________" << endl;
}

#endif //FLATMAP_FLATMAP_H
