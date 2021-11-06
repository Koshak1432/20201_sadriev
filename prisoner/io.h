#ifndef PRISONER_IO_H
#define PRISONER_IO_H

#include <fstream>
#include "game.h"


std::string read_line(std::ifstream &stream);
Matrix read_matrix(const std::string &file_path);

#endif //PRISONER_IO_H
