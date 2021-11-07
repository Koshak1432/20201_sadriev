cmake_minimum_required(VERSION 3.20)
project(prisoner)

set(CMAKE_CXX_STANDARD 17)

add_executable(prisoner main.cpp factory.h game_runner.cpp game_runner.h console_interface.cpp console_interface.h strategies/cooperate.cpp strategies/cooperate.h strategies/defect.cpp strategies/defect.h strategies/change.cpp strategies/change.h strategy.h game.cpp game.h io.cpp io.h strategies/random.cpp strategies/random.h strategies/majority.cpp strategies/majority.h strategies/mimic.cpp strategies/mimic.h strategies/patient.cpp strategies/patient.h config_provider.cpp config_provider.h strategies/election.cpp strategies/election.h strategy_factory.h)
