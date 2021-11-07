#include "io.h"

static std::string read_line(std::ifstream &stream)
{
	std::string line;
	std::getline(stream, line);
	return line;
}

std::size_t read_size_t(std::ifstream &stream)
{
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

Matrix read_matrix(const std::string &file_path)
{
	if (file_path.empty())
	{
		return Matrix{};
	}
	std::ifstream stream(file_path);
	if (!stream.is_open())
	{
		throw std::invalid_argument("can't open matrix file\n");
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
			throw std::invalid_argument("invalid matrix\n");
		}
	}
	return matrix;
}