#ifndef PRISONER_IO_H
#define PRISONER_IO_H

#include <fstream>
#include "game.h"

std::size_t read_size_t(std::ifstream &stream);
std::string read_string(std::ifstream &stream);
Matrix read_matrix(const std::string &file_path);

#endif //PRISONER_IO_H
