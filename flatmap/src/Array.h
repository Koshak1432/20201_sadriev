#ifndef FLATMAP_ARRAY_H
#define FLATMAP_ARRAY_H

using std::size_t;
using std::cout;
using std::endl;

template<class T>
class Array
{
public:
	explicit Array(size_t size = 1); // takes array size as parameter
	Array(const Array<T> &other); // copy ctor
	Array(Array<T> &&other) noexcept; //move ctor
	~Array();

	void resize(size_t new_capacity); //makes an array with new_capacity and copies elements from the old one
	std::size_t get_size() const noexcept; //gets a size of an array
	void push_back(T elem);
	void erase(size_t idx) noexcept;
	void insert(size_t idx, const T &value);
	Array<T> &operator =(const Array<T> &other); //assigns the fields of the left array to the fields of the other array
	Array<T> &operator =(Array<T> &&other) noexcept; //move assignment operator
	T &operator [](size_t idx) noexcept; //returns the element by the idx
	const T &operator [](size_t idx) const;

private:
	T *data_ = nullptr;
	size_t capacity_ = 0;
	size_t size_ = 0; //how many elements in the array
	void make_shift_right(size_t idx);
};

template<class T>
Array<T>::Array(size_t size) : capacity_ (size)
{
	data_ = new T[capacity_];
}

template<class T>
Array<T>::Array(const Array<T> &other) : capacity_ (other.capacity_), size_ (other.size_)
{
	data_ = new T[other.capacity_];
	for (size_t i = 0; i < other.size_; ++i)
	{
		data_[i] = other.data_[i];
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
std::size_t Array<T>::get_size() const noexcept
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
void Array<T>::erase(size_t idx) noexcept
{
	assert(idx < capacity_);
	for (size_t i = idx; i < size_; ++i)
	{
		data_[i] = data_[i + 1];
	}
	--size_;
}

template<class T>
Array<T> &Array<T>::operator =(const Array<T> &other)
{
	if (&other != this)  //checking for self-assignment
	{
		delete[] data_;
		size_ = other.size_;
		capacity_ = other.capacity_;
		data_ = new T[other.capacity_];

		for (size_t i = 0; i < other.size_; ++i)
		{
			data_[i] = other.data_[i];
		}
	}

	return *this;
}

template<class T>
T &Array<T>::operator [](size_t idx) noexcept
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
void Array<T>::insert(size_t idx, const T &value)
{
	if (++size_ == capacity_)
	{
		resize(capacity_ * 2);
	}
	make_shift_right(idx);
	data_[idx] = value;
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
