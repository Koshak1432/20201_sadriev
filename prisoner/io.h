#ifndef PRISONER_IO_H
#define PRISONER_IO_H

#include <fstream>
#include "game.h"

//class Matrix;

std::string read_line(std::ifstream stream);
Matrix read_matrix(std::ifstream stream);


#endif //PRISONER_IO_H
