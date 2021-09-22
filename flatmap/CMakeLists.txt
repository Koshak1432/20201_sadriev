cmake_minimum_required(VERSION 3.20)
project(flatmap)

add_subdirectory(Google_tests)
set(CMAKE_CXX_STANDARD 14)

add_executable(flatmap main.cpp src/Array.h src/FlatMap.h)
