#ifndef FLATMAP_ARRAY_H
#define FLATMAP_ARRAY_H

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
	for (size_t i = 0; i < capacity_ - 1; ++i)
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
	for (size_t i = idx; i < load_; ++i)
	{
		data_[i] = data_[i + 1];
	}
	--load_;
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

#endif //FLATMAP_ARRAY_H
