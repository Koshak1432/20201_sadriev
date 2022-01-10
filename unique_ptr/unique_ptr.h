#ifndef UNIQUE_PTR_UNIQUE_PTR_H
#define UNIQUE_PTR_UNIQUE_PTR_H

#include <iostream>

// RAII

template<class T>
class uni_ptr
{
public:
	explicit uni_ptr(T *p = nullptr) noexcept;
	uni_ptr(uni_ptr<T> &other) = delete;
	uni_ptr(uni_ptr<T> &&other) noexcept;
	~uni_ptr();

	T *get() const noexcept;
	T *release() noexcept;
	void reset(T *new_p) noexcept;
	void swap(uni_ptr<T> &other) noexcept;

	T *operator ->() const;
	T &operator *() const;
	explicit operator bool() const noexcept;
	uni_ptr<T> &operator =(uni_ptr<T> &&other) noexcept;
	uni_ptr<T> &operator =(uni_ptr<T> &other) = delete;
private:
	T *p_ = nullptr;
};

template<class T>
T *uni_ptr<T>::get() const noexcept
{
	return p_;
}

template<class T>
void uni_ptr<T>::reset(T *new_p) noexcept
{
	delete p_;
	p_ = new_p;
}

template<class T>
T *uni_ptr<T>::release() noexcept
{
	T * tmp_p = p_;
	p_ = nullptr;
	return tmp_p;
}

template<class T>
T &uni_ptr<T>::operator *() const
{
	return *p_;
}

template<class T>
T *uni_ptr<T>::operator ->() const
{
	return p_;
}

template<class T>
uni_ptr<T>::uni_ptr(uni_ptr<T> &&other) noexcept
{
	p_ = other.release();
}

template<class T>
uni_ptr<T>::operator bool() const noexcept
{
	return nullptr != p_;
}

template<class T>
uni_ptr<T> &uni_ptr<T>::operator =(uni_ptr<T> &&other) noexcept
{
	p_ = other.release();
}

template<class T>
void uni_ptr<T>::swap(uni_ptr<T> &other) noexcept
{
	std::swap<T>(p_, other.p_);
}

template<class T>
uni_ptr<T>::~uni_ptr()
{
	delete p_;
}

template<class T>
uni_ptr<T>::uni_ptr(T *p) noexcept :p_(p)
{}

#endif //UNIQUE_PTR_UNIQUE_PTR_H
