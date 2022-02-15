cmake_minimum_required(VERSION 3.20)
project(kek)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS} -fsanitize=address -fsanitize=leak -fsanitize=undefined -static-libasan -Wall -Wpedantic -g")
add_executable(kek main.cpp)