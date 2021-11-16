#ifndef FLATMAP_ARRAY_H
#define FLATMAP_ARRAY_H

template<class T>
class Array
{
public:
	// takes array size as parameter
	explicit Array(std::size_t size = 1);
	Array(const Array<T> &other);
	Array(Array<T> &&other) noexcept;
	~Array();

	//makes an array with new_capacity and copies elements from the old one
	void resize(std::size_t new_capacity);
	//gets a size of an array
	std::size_t get_size() const noexcept;
	bool empty() const noexcept;
	void push_back(T elem);


	void erase(std::size_t idx) noexcept;
	void insert(std::size_t idx, const T &value);
	void clear() noexcept;
	void swap(Array<T> &other) noexcept;

	//assigns the fields of the left array to the fields of the other array
	Array<T> &operator =(const Array<T> &other);
	//move assignment operator
	Array<T> &operator =(Array<T> &&other) noexcept;
	//returns the element by the idx
	T &operator [](std::size_t idx) noexcept;
	const T &operator [](std::size_t idx) const;

private:
	T *data_ = nullptr;
	std::size_t capacity_ = 0;
	std::size_t size_ = 0; 			//how many elements in the array
	void make_shift_right(std::size_t idx);
};

template<class T>
Array<T>::Array(std::size_t size) : capacity_(size)
{
	data_ = new T[capacity_];
}

template<class T>
Array<T>::Array(const Array<T> &other) : capacity_(other.capacity_), size_(other.size_)
{
	data_ = new T[other.capacity_];
	std::copy(other.data_, other.data_ + other.size_, data_);
}

template<class T>
Array<T>::~Array<T>()
{
	clear();
}

template<class T>
void Array<T>::resize(std::size_t new_capacity)
{
	T *temp = new T[new_capacity];
	if (!empty())
	{
		std::copy(data_, data_ + capacity_ - 1, temp);
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
	if (0 == capacity_)
	{
		return;
	}
	std::size_t multiplier = 2;
	if (++size_ == capacity_)
	{
		data_ = resize(capacity_ * multiplier);
	}
	data_[size_ - 1] = elem;
}

template<class T>
void Array<T>::erase(std::size_t idx) noexcept
{
	assert(idx < capacity_);
	for (std::size_t i = idx; i < size_; ++i)
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

		std::copy(other.data_, other.data_ + other.size_, data_);
	}
	return *this;
}

template<class T>
T &Array<T>::operator [](std::size_t idx) noexcept
{
	assert(idx < capacity_ && idx >= 0);
	return data_[idx];
}

template<class T>
const T &Array<T>::operator [](std::size_t idx) const
{
	assert(idx < capacity_ && idx >= 0);
	return data_[idx];
}

template<class T>
void Array<T>::insert(std::size_t idx, const T &value)
{
	std::size_t multiplier = 2;
	if (0 == capacity_)
	{
		resize(multiplier * multiplier);
	}
	if (++size_ == capacity_)
	{
		resize(capacity_ * multiplier);
	}
	make_shift_right(idx);
	data_[idx] = value;
}

template<class T>
void Array<T>::make_shift_right(std::size_t idx) //[idx + 1] = [idx], not [idx] = [idx - 1]
{
	assert(idx >= 0 && idx < capacity_);
	for (std::size_t i = size_ - 1; i > idx; --i)
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
		delete[] data_;
		data_ = other.data_;
		capacity_ = other.capacity_;
		size_ = other.size_;
		other.data_ = nullptr;
		other.size_ = 0;
		other.capacity_ = 0;
	}
	return *this;
}

template<class T>
void Array<T>::clear() noexcept
{
	delete[] data_;
	data_ = nullptr;
	size_ = capacity_ = 0;
}

template<class T>
bool Array<T>::empty() const noexcept
{
	return size_ == 0;
}

template<class T>
void Array<T>::swap(Array<T> &other) noexcept
{
	std::swap<T*>(data_, other.data_);
	std::swap<std::size_t>(capacity_, other.capacity_);
	std::swap<std::size_t>(size_, other.size_);
}

#endif //FLATMAP_ARRAY_H
