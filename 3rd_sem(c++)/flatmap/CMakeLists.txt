cmake_minimum_required(VERSION 3.20)
project(flatmap)

add_subdirectory(Google_tests)
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fsanitize=address -fsanitize=leak -fsanitize=undefined -static-libasan -Wall -Werror -Wpedantic -g")

add_executable(flatmap main.cpp src/Array.h src/FlatMap.h)
