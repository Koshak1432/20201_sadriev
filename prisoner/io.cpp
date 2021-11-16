#include "io.h"

#include "game.h"

constexpr std::size_t ROWS = 8;

static std::string read_line(std::ifstream &stream)
{
	assert(stream.is_open());
	std::string line;
	std::getline(stream, line);
	return line;
}

std::size_t read_size_t(std::ifstream &stream)
{
	assert(stream.is_open());
	return std::stoul(read_line(stream));
}

std::string read_string(std::ifstream &stream)
{
	std::string string = read_line(stream);

	if ('\r' == string[string.size() - 1])
	{
		string.pop_back();
	}
	return string;
}

Matrix read_matrix(const std::string &file_name)
{
	if (file_name.empty())
	{
		return Matrix{};
	}
	std::ifstream stream(file_name);
	if (!stream.is_open())
	{
		throw std::invalid_argument("can't open matrix file");
	}
	stream.exceptions(std::ios::badbit | std::ios::failbit);
	Matrix matrix{};
	std::vector<int> inputs(COLS, 0);
	std::vector<int> row(inputs);

	for (std::size_t i = 0; i < ROWS; ++i)
	{
		if (stream >> inputs[0] >> inputs[1] >> inputs[2])
		{
			for (std::size_t j = 0; j < COLS; ++j)
			{
				row[j] = inputs[j];
			}
			matrix[i] = row;
		}
		else
		{
			throw std::invalid_argument("invalid matrix");
		}
	}
	return matrix;
}