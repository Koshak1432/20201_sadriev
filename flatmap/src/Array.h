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
	Array(Array<T> &&other) noexcept;
	~Array();

	void resize(size_t new_capacity);
	std::size_t get_size() const;
	void push_back(T elem);
	void erase(size_t idx);
	bool insert(size_t idx, const T &value);
	Array<T> &operator =(const Array<T> &orig);
	Array<T> &operator =(Array<T> &&other) noexcept;
	T &operator [](size_t idx);
	const T &operator [](size_t idx) const;

private:
	T *data_ = nullptr;
	size_t size_ = 0; //how many elements in array
	size_t capacity_ = 0; //complete size
	void make_shift_right(size_t idx);
};

template<class T>
Array<T>::Array(size_t size) : capacity_ {size}
{
	data_ = new T[capacity_];
}

template<class T>
Array<T>::Array(const Array<T> &orig) : size_ {orig.size_}, capacity_ {orig.capacity_}
{
	data_ = new T[orig.capacity_];
	for (size_t i = 0; i < orig.size_; ++i)
	{
		data_[i] = orig.data_[i];
	}
}

template<class T>
Array<T>::~Array<T>()
{
	delete[] data_;
}

template<class T>
void Array<T>::resize(size_t new_capacity)
{
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
void Array<T>::erase(size_t idx)
{
	assert(idx < capacity_);
	for (size_t i = idx; i < size_; ++i)
	{
		data_[i] = data_[i + 1];
	}
	--size_;
}

template<class T>
Array<T> &Array<T>::operator =(const Array<T> &orig)
{
	if (&orig != this)  //checking for self-assignment
	{
		delete[] data_;
		size_ = orig.size_;
		capacity_ = orig.capacity_;
		data_ = new T[orig.capacity_];

		for (size_t i = 0; i < orig.size_; ++i)
		{
			data_[i] = orig.data_[i];
		}
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
	if (++size_ == capacity_)
	{
		resize(capacity_ * 2);
	}
	make_shift_right(idx);
	data_[idx] = value;
	return true;
}

template<class T>
void Array<T>::make_shift_right(size_t idx) //[idx + 1] = [idx], not [idx] = [idx - 1]
{
	assert(idx >= 0 && idx < capacity_);
	for (size_t i = size_ - 1; i > idx; --i)
	{
		data_[i] = data_[i - 1];
	}
}

template<class T>
Array<T>::Array(Array<T> &&other) noexcept :capacity_(other.capacity_), size_(other.size_)
{
	data_ = other.data_;
	other.data_ = nullptr;
	other.size_ = 0;
	other.capacity_ = 0;
}

template<class T>
Array<T> &Array<T>::operator =(Array<T> &&other) noexcept
{
	if (&other != this)
	{
		delete []data_;
		data_ = other.data_;
		capacity_ = other.capacity_;
		size_ = other.size_;
		other.data_ = nullptr;
		other.size_ = 0;
		other.capacity_ = 0;
	}
	return *this;
}

#endif //FLATMAP_ARRAY_H
