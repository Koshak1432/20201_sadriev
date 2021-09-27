#ifndef FLATMAP_FLATMAP_H
#define FLATMAP_FLATMAP_H
#include "Array.h"
#include <cstring>
#include <iostream>

template<class Key, class Value>
class FlatMap
{
public:
	explicit FlatMap(size_t size = 1);
	~FlatMap() = default;

	FlatMap(const FlatMap<Key, Value> &other);
	//FlatMap(FlatMap &&other);

	void swap(FlatMap<Key, Value> &other);

	FlatMap<Key, Value> &operator =(const FlatMap<Key, Value> &other);
	//FlatMap<Key, Value> &&operator=(FlatMap<Key, Value> &&other);

	void clear();
	bool erase(const Key &key);
	bool insert(const Key &key, const Value &value);
	bool contains(const Key &key) const;

	Value &operator [](const Key &key);

	Value &at(const Key &key);
	const Value &at(const Key &key) const;

	size_t size() const; //get size of the container
	bool empty() const; //is container empty

	friend bool operator ==(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second)
	{
		if (first.load_ != second.load_)
		{
			return false;
		}
		for (size_t i = 0; i < first.load_; ++i)
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
	size_t size_ = 0;
	size_t load_ = 0;

	size_t bin_search(const Key &key) const;
	void resize(size_t new_size);
};

template<class Key, class Value>
bool FlatMap<Key, Value>::empty() const
{
	return load_ == 0;
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::size() const
{
	return load_;
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::bin_search(const Key &key) const
{
	size_t count = load_ - 1;
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
FlatMap<Key, Value>::FlatMap(size_t size) : key_arr_(size), val_arr_(size), size_{size}
{
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(const FlatMap<Key, Value> &other) :size_{other.size_}
{
	key_arr_ = Array<Key>(other.size_);
	val_arr_ = Array<Value>(other.size_);
	for (size_t i = 0; i < other.size; ++i)
	{
		key_arr_[i] = other.key_arr_[i];
		val_arr_[i] = other.val_arr_[i];
	}
}

template<class Key, class Value>
FlatMap<Key, Value> &FlatMap<Key, Value>::operator =(const FlatMap<Key, Value> &other)
{
	if (&other != this)
	{
		key_arr_ = other.key_arr_;
		val_arr_ = other.val_arr_;
		size_ = other.size_;
		load_ = other.load_;
	}
	return *this;
}

template<class Key, class Value>
void FlatMap<Key, Value>::clear() //erase for every key
{
	// std::fill
	memset(&key_arr_, 0, sizeof(Key) * load_); //???????? todo
	memset(&val_arr_, 0, sizeof(Value) * load_);
	size_ = 0;
	load_ = 0;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::contains(const Key &key) const
{
	size_t idx = bin_search(key);
	return (key == key_arr_[idx]);
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
void FlatMap<Key, Value>::resize(size_t new_size)
{
	cout << "RESIZE FROM FLATMAP" << endl;
	key_arr_.resize(new_size);
	val_arr_.resize(new_size);
	size_ = new_size;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::insert(const Key &key, const Value &value)
{
	if (++load_ == size_)
	{
		resize(size_ * 2);
	}
	size_t idx = bin_search(key);
	if (key_arr_[idx] == key)
	{
		cout << key << " already in the container" << endl;
		return false;
	}
	cout << "insert in flatmap at idx == " << idx << endl;
	key_arr_.insert(idx, key);
	val_arr_.insert(idx, value);
	return true;
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::at(const Key &key)
{
	size_t idx = bin_search(key);
	if (idx < 0 || idx > load_)
	{
		//XANA
		//throw an exception
	}
	return val_arr_[idx];
}

template<class Key, class Value>
const Value &FlatMap<Key, Value>::at(const Key &key) const
{
	size_t idx = bin_search(key);
	if (idx < 0 || idx > load_)
	{
		//XANA
		//throw an exception
	}
	return val_arr_[idx];
}

template<class Key, class Value>
Value &FlatMap<Key, Value>::operator [](const Key &key)
{
	size_t idx = bin_search(key);
	assert(idx < load_ && idx >= 0);
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
	size_t tmp_size = size_;
	size_t tmp_load = load_;
	key_arr_ = other.key_arr_;
	val_arr_ = other.val_arr_;
	size_ = other.size_;
	load_ = other.load_;
	other.key_arr_ = tmp_key;
	other.val_arr_ = tmp_val;
	other.size_ = tmp_size;
	other.load_ = tmp_load;
}

template<class Key, class Value>
void FlatMap<Key, Value>::print_flatmap()
{
	cout << "___________________________________" << endl;
	for (size_t i = 0; i < load_; ++i)
	{
		cout << "idx " << i <<  " | key " << key_arr_[i] << " | value " << val_arr_[i] << endl;
	}
	cout << "___________________________________" << endl;
}

#endif //FLATMAP_FLATMAP_H
