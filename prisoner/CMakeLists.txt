cmake_minimum_required(VERSION 3.20)
project(prisoner)

set(CMAKE_CXX_STANDARD 14)

add_executable(prisoner main.cpp factory.cpp factory.h game_runner.cpp game_runner.h console_interface.cpp console_interface.h)
