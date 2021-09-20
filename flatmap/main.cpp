#include <iostream>
#include <cassert>
#include <cstring>

using std::size_t;
using std::cout;
using std::endl;

template<class T>
class Array
{
public:
	explicit Array(std::size_t size = 1);
	Array(const Array<T> &orig);
	~Array();

	T *resize(std::size_t new_capacity);
	T get_by_idx (std::size_t idx) const;
	std::size_t get_size() const;
	void push_back(T elem);
	void erase(std::size_t idx);

	Array<T> &operator =(const Array<T> &orig);
	Array<T> &operator [](const size_t idx);
	const Array<T> &operator [](const size_t idx) const;


private:
	T *data_ = nullptr;
	std::size_t load_ = 0;
	std::size_t capacity_ = 0;
};

template<class T>
Array<T>::Array(std::size_t size) : capacity_ {size}
{
	data_ = new T[capacity_];
	std::cout << "default constructor" << std::endl;
}

template<class T>
Array<T>::Array(const Array<T> &orig) : load_ {orig.load_}, capacity_ {orig.capacity_}
{
	data_ = new T[orig.capacity_];
	for (std::size_t i = 0; i < orig.load_; ++i)
	{
		data_[i] = orig.data_[i];
	}
	std::cout << "copy constructor!" << std::endl;
}

template<class T>
Array<T>::~Array<T>()
{
	std::cout << "destructor" << std::endl;
	delete[] data_;
}

template<class T>
T *Array<T>::resize(std::size_t new_capacity)
{
	std::cout << "RESIZE!" << std::endl;

	T *temp = new T[new_capacity];
	for (std::size_t i = 0; i < capacity_; ++i)
	{
		temp[i] = data_[i];
	}
	capacity_ = new_capacity;
	delete[] data_;
	return temp;
}

template<class T>
T Array<T>::get_by_idx(std::size_t idx) const
{
	assert(idx < capacity_);
	return data_[idx];
}

template<class T>
std::size_t Array<T>::get_size() const
{
	return capacity_;
}

template<class T>
void Array<T>::push_back(T elem)
{
	if (load_ == capacity_)
	{
		data_ = resize(capacity_ * 2);
	}
	data_[load_++] = elem;
}

template<class T>
void Array<T>::erase(std::size_t idx)
{
	assert(idx < capacity_);
	--load_;
	for (std::size_t i = 0; i < load_; ++i)
	{
		data_[i] = data_[i + 1];
	}
}

template<class T>
Array<T> &Array<T>::operator=(const Array<T> &orig)
{
	if (&orig != this)  //checking for self-assignment
	{
		delete[] data_;
		load_ = orig.load_;
		data_ = new T[orig.capacity_];

		for (std::size_t i = 0; i < orig.load_; ++i)
		{
			data_[i] = orig.data_[i];
		}

		std::cout << "operator =" << std::endl;
	}

	return *this;
}

template<class T>
Array<T> &Array<T>::operator [](const size_t idx)
{
	return data_[idx];
}

template<class T>
const Array<T> &Array<T>::operator [](const size_t idx) const
{
	return data_[idx];
}

void print_arr(const Array<int> &arr)
{
	for (std::size_t i = 0; i < arr.get_size(); ++i)
	{
		std::cout << arr.get_by_idx(i) << "\t";
	}
	std::cout << std::endl;
}


template<class Key, class Value>
class FlatMap
{
public:
	explicit FlatMap(size_t size = 1);
	~FlatMap();

	FlatMap(const FlatMap<Key, Value> &other);
	//FlatMap(FlatMap &&other);

	void swap(FlatMap<Key, Value> &other);

	FlatMap<Key, Value> &operator=(const FlatMap<Key, Value> &other);
	//FlatMap<Key, Value> &&operator=(FlatMap<Key, Value> &&other);

	void clear();
	bool erase(const Key &key);
	bool insert(const Key &key, const Value &value);
	bool contains(const Key &key) const;

	Value &operator[](const Key &key);

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
		if (first.load_ == second.load_)
		{
			return true;
		}
		for (size_t i = 0; i < first.load_; ++i)
		{
			if (first.key_arr_[i] == second.key_arr_[i] || first.val_arr_[i] == second.val_arr_[i])
			{
				return true;
			}
		}
		return false;
	}

private:
	Array<Key> *key_arr_;
	Array<Value> *val_arr_;
	size_t size_ = 0;
	size_t load_ = 0;

	size_t bin_search(const Key &key);

	void make_shift_left(size_t idx);
	void make_shift_right(size_t idx);
	void resize(size_t new_size);
};

template<class Key, class Value>
void FlatMap<Key, Value>::make_shift_left(size_t idx)
{
	assert(idx >= 0);
	for (size_t i = idx; i < load_ - 1; ++i)
	{
		key_arr_[i] = key_arr_[i + 1];
		val_arr_[i] = val_arr_[i + 1];
	}
}

template<class Key, class Value>
void FlatMap<Key, Value>::make_shift_right(size_t idx)
					//[idx + 1] = [idx], not [idx] = [idx - 1]
{
	assert(idx >= 0);
	for (size_t i = load_ - 1; i > idx; --i)
	{
		key_arr_[i] = key_arr_[i - 1];
		val_arr_[i] = val_arr_[i - 1];
	}
}

template<class Key, class Value>
bool FlatMap<Key, Value>::empty() const
{
	return load_ == 0;
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::size() const
{
	return size_;
}

template<class Key, class Value>
size_t FlatMap<Key, Value>::bin_search(const Key &key)
{
	size_t left = 0;
	size_t right = load_ - 1;
	while (left <= right)
	{
		size_t mid = right / 2 + left / 2;
		if (key_arr_[mid] == key)
		{
			return mid;
		}
		if (left == right)
		{
			return left + 1;
		}
		if (key < key_arr_[mid])
		{
			right = mid - 1;
		}
		else
		{
			left = mid + 1;
		}
	}
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(size_t size) : size_{size}
{
	key_arr_ = new Array<Key>(size);
	val_arr_ = new Array<Value>(size);
}

template<class Key, class Value>
FlatMap<Key, Value>::~FlatMap()
{
	delete[] key_arr_;
	delete[] val_arr_;
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(const FlatMap<Key, Value> &other) :size_{other.size_}
{
	key_arr_ = new Key[other.size_];
	val_arr_ = new Value[other.size_];
	for (size_t i = 0; i < other.size; ++i)
	{
		key_arr_[i] = other.key_arr_[i];
		val_arr_[i] = other.val_arr_[i];
	}
}

template<class Key, class Value>
FlatMap<Key, Value> &FlatMap<Key, Value>::operator=(const FlatMap<Key, Value> &other)
{
	if (&other == this)
	{
		return *this;
	}

	delete[] key_arr_;
	delete[]val_arr_;
	size_ = other.size_;
	key_arr_ = new Key[other.size_];
	val_arr_ = new Value[other.size_];

	for (size_t i = 0; i < other.size_; ++i)
	{
		key_arr_[i] = other.key_arr_[i];
		val_arr_[i] = other.val_arr_[i];
	}
	return *this;
}

template<class Key, class Value>
void FlatMap<Key, Value>::clear()
{
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
		make_shift_left(idx);
		return true;
	}
	return false;
}

template<class Key, class Value>
bool FlatMap<Key, Value>::insert(const Key &key, const Value &value)
{
	if (++load_ == size_)
	{
		resize(size_ * 2);
	}
	size_t idx = bin_search(key);
	make_shift_right(idx);
	key_arr_[idx] = key;
	val_arr_[idx] = value;
	return true;
}

template<class Key, class Value>
void FlatMap<Key, Value>::resize(size_t new_size)
{
	Array<Key> *tmp_key = new Key[new_size];
	Array<Value> *tmp_val = new Value[new_size];
	for (size_t i = 0; i < size_; ++i)
	{
		tmp_key[i] = key_arr_[i];
		tmp_val[i] = val_arr_[i];
	}
	size_ = new_size;
	delete[] key_arr_;
	delete[] val_arr_;
	key_arr_ = tmp_key;
	val_arr_ = tmp_val;
}

int main()
{

	return 0;
}
