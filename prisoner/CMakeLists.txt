cmake_minimum_required(VERSION 3.20)
project(prisoner)

set(CMAKE_CXX_STANDARD 17)

add_executable(prisoner main.cpp factory.h game_runner.cpp game_runner.h console_interface.cpp console_interface.h cooperate.cpp cooperate.h defect.cpp defect.h change.cpp change.h strategy.h game.cpp game.h io.cpp io.h random.cpp random.h majority.cpp majority.h mimic.cpp mimic.h endurer.cpp endurer.h config_provider.cpp config_provider.h)
