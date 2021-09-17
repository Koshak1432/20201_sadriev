#include <iostream>
#include <cassert>

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

	Array<T> &operator = (const Array<T> &orig);

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
	FlatMap();
	~FlatMap();

	FlatMap(const FlatMap<Key, Value> &other);
	FlatMap(FlatMap &&other);

	void swap(FlatMap<Key, Value> &other);

	FlatMap<Key, Value> &operator=(const FlatMap<Key, Value>);
	FlatMap<Key, Value> &&operator=(FlatMap<Key, Value> &&other);

	void clear();
	bool erase(const Key &key);
	bool insert(const Key &key, const Value &value);

	bool contains(const Key &key) const;

	Value &operator[](const Key &key);

	Value &at(const Key &key);

	const Value &at(const Key &key) const;

	size_t size() const; //get size of container
	bool empty() const; //is container empty

	friend bool operator ==(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second);
	friend bool operator !=(const FlatMap<Key, Value> &first, const FlatMap<Key, Value> &second);

private:
	Array<Value> arr_;
	size_t size_ = 0;
	size_t load_ = 0;
};

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


int main()
{

	return 0;
}
