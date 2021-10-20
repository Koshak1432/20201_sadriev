cmake_minimum_required(VERSION 3.20)
project(prisoner)

set(CMAKE_CXX_STANDARD 17)

add_executable(prisoner main.cpp factory.h game_runner.cpp game_runner.h console_interface.cpp console_interface.h cooperate.cpp cooperate.h defect.cpp defect.h tit_for_tat.cpp tit_for_tat.h strategy.h)
