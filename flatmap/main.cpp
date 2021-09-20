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

	Array<T> &operator=(const Array<T> &orig);

private:
	T *data_ = nullptr;
	std::size_t size_ = 0;
	std::size_t capacity_ = 0;
};

template<class T>
Array<T>::Array(std::size_t size) : capacity_ {size}
{
	data_ = new T[capacity_];
	std::cout << "default constructor" << std::endl;
}

template<class T>
Array<T>::Array(const Array<T> &orig) : size_ {orig.size_}, capacity_ {orig.capacity_}
{
	data_ = new T[orig.capacity_];
	for (std::size_t i = 0; i < orig.size_; ++i)
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
	return size_;
}

template<class T>
void Array<T>::push_back(T elem)
{
	if (size_ == capacity_)
	{
		data_ = resize(capacity_ * 2);
	}
	data_[size_++] = elem;
}

template<class T>
void Array<T>::erase(std::size_t idx)
{
	assert(idx < capacity_);
	--size_;
	for (std::size_t i = 0; i < size_; ++i)
	{
		data_[i] = data_[i + 1];
	}
}

template<class T>
Array<T> &Array<T>::operator =(const Array<T> &orig)
{
	if (&orig != this)  //checking for self-assignment
	{
		delete[] data_;
		size_ = orig.size_;
		data_ = new T[orig.capacity_];

		for (std::size_t i = 0; i < orig.size_; ++i)
		{
			data_[i] = orig.data_[i];
		}

		std::cout << "operator =" << std::endl;
	}

	return *this;
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

	friend bool operator ==(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second);
	friend bool operator !=(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second);

private:
	Array<Key> key_arr_;
	Array<Value> val_arr_;
	size_t size_ = 0;
	size_t load_ = 0;

	bool bin_search(const Key &key, size_t &idx); //??????????????

	void make_shift_left(size_t idx);
	void make_shift_right(size_t idx);


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
bool FlatMap<Key, Value>::bin_search(const Key &key, size_t &idx)
{
	size_t left = 0;
	size_t right = load_ - 1;
	while (left <= right)
	{
		size_t mid = right / 2 + left / 2;
		if (key_arr_[mid] == key)
		{
			idx = mid;
			return true;
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
	idx = load_; //?????????????
	return false;
}

template<class Key, class Value>
FlatMap<Key, Value>::FlatMap(size_t size) : size_{size}
{
	key_arr_ = new Key[size];
	val_arr_ = new Value[size];
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
	size_t idx = 0;
	return bin_search(key, idx);
}

template<class Key, class Value>
bool FlatMap<Key, Value>::erase(const Key &key)
{
	size_t idx = 0;
	if (bin_search(key, idx))
	{
		make_shift_left(idx);
		return true;
	}
	return false;
}




int main()
{

	return 0;
}
