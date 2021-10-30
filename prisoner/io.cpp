#include "io.h"


std::string read_line(std::ifstream stream)
{
	std::string line;
	std::getline(stream, line);
	return line;
}

Matrix read_matrix(std::ifstream stream)
{
	Matrix matrix(ROWS, COLS);
	int first = 0;
	int second = 0;
	int third = 0;
	std::vector<int> vec(3, 0);

	for (std::size_t i = 0; i < ROWS; ++i)
	{
		if (stream >> first >> second >> third)
		{
			vec[0] = first;
			vec[1] = second;
			vec[2] = third;
			matrix[i] = vec;
		}
		else
		{
			throw std::invalid_argument("invalid matrix");
		}
	}
}