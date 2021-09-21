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
	explicit Array(size_t size = 1);
	Array(const Array<T> &orig);
	~Array();

	void resize(size_t new_capacity);
	std::size_t get_size() const;
	void push_back(T elem);
	void erase(size_t idx);
	bool insert(size_t idx, const T &value);
	Array<T> &operator =(const Array<T> &orig);
	T &operator [](size_t idx);
	const T &operator [](size_t idx) const;

private:
	T *data_ = nullptr;
	size_t load_ = 0; //how many elements in array
	size_t capacity_ = 0; //complete size
	void make_shift_right(size_t idx);
};

template<class T>
Array<T>::Array(size_t size) : capacity_ {size}
{
	data_ = new T[capacity_];
	cout << "default constructor with size " << size << endl;
}

template<class T>
Array<T>::Array(const Array<T> &orig) : load_ {orig.load_}, capacity_ {orig.capacity_}
{
	data_ = new T[orig.capacity_];
	for (size_t i = 0; i < orig.load_; ++i)
	{
		data_[i] = orig.data_[i];
	}
	cout << "copy constructor!" << endl;
}

template<class T>
Array<T>::~Array<T>()
{
	cout << "destructor" << endl;
	delete[] data_;
}

template<class T>
void Array<T>::resize(size_t new_capacity)
{
	cout << "RESIZE with type " << typeid(T).name() << endl;

	T *temp = new T[new_capacity];
	for (size_t i = 0; i < capacity_; ++i)
	{
		temp[i] = data_[i];
	}
	capacity_ = new_capacity;
	delete[] data_;
	data_ = temp;
}

template<class T>
std::size_t Array<T>::get_size() const
{
	return load_;
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
void Array<T>::erase(size_t idx)
{
	assert(idx < capacity_);
	--load_;
	for (size_t i = idx; i < load_; ++i)
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
		load_ = orig.load_;
		capacity_ = orig.capacity_;
		data_ = new T[orig.capacity_];

		for (size_t i = 0; i < orig.load_; ++i)
		{
			data_[i] = orig.data_[i];
		}

		cout << "operator =" << endl;
	}

	return *this;
}

template<class T>
T &Array<T>::operator [](size_t idx)
{
	assert(idx < capacity_ && idx >= 0);
	return data_[idx];
}

template<class T>
const T &Array<T>::operator [](size_t idx) const
{
	assert(idx < capacity_ && idx >= 0);
	return data_[idx];
}

template<class T>
bool Array<T>::insert(size_t idx, const T &value)
{
	if (++load_ == capacity_)
	{
		resize(capacity_ * 2);
	}
	make_shift_right(idx);
	cout << "inserted in Array at idx == " << idx << " value == " << value << endl;
	data_[idx] = value;
	return true;
}

template<class T>
void Array<T>::make_shift_right(size_t idx) //[idx + 1] = [idx], not [idx] = [idx - 1]
{
	assert(idx >= 0 && idx < capacity_);
	for (size_t i = load_ - 1; i > idx; --i)
	{
		data_[i] = data_[i - 1];
	}
}

//void print_arr(const Array<int> &arr)
//{
//	for (size_t i = 0; i < arr.get_size(); ++i)
//	{
//		cout << arr.get_by_idx(i) << "\t";
//	}
//	cout << endl;
//}


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

	void print_flatmap()
	{
		cout << "___________________________________" << endl;
		for (size_t i = 0; i < load_; ++i)
		{
			cout << "idx " << i <<  "| key " << key_arr_[i] << " | " << "value " << val_arr_[i] << endl;
		}
		cout << "___________________________________" << endl;
	}

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
	size_t left = 0;
	size_t right = load_ - 1;
	if (1 == load_)
	{
		return 0;
	}
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
			if (0 != mid)
			{
				right = mid - 1;
				continue;
			}
			right = mid;
		}
		else
		{
			left = mid + 1;
		}
	}
	return load_ - 1;
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

int main()
{
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "Johan";
	std::string str4 = "John";
	std::string str5 = "Keka";
	std::string str6 = "Nipsey";
	std::string str7 = "Yo";
	FlatMap<std::string, int> my_map(5);
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	my_map.insert(str4, 4);
	my_map.print_flatmap();
	my_map.insert(str5, 5);
	my_map.insert(str6, 6);
	my_map.print_flatmap();
	cout << my_map.contains(str5) << endl;
	cout << my_map.contains(str7) << endl;

	return 0;
}
