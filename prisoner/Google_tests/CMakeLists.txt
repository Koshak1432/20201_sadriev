project(Google_tests)

set(CMAKE_CXX_STANDARD 17)
add_subdirectory(lib)
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

add_executable(Google_Tests_run prisoner_tests.cpp ../factory.h ../game_runner.cpp ../game_runner.h ../console_interface.cpp ../console_interface.h ../strategy.h ../game.cpp ../game.h ../io.cpp ../io.h ../config_provider.cpp ../config_provider.h)
target_link_libraries(Google_Tests_run gtest gtest_main)