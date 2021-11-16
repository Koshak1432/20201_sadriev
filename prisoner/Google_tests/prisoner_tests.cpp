#include "gtest/gtest.h"
#include "../game_runner.h"
#include "../io.h"
#include "../console_interface.h"
#include "../config_provider.h"

TEST(prison, parse_args_check)
{
	std::vector<const char *> argv = {"blank", "change", "random", "mimic", "--mode=fast", "--steps=30"};
	CLI ui;
	Args args = ui.parse_args(6, const_cast<char **>(argv.data()));
	EXPECT_EQ(args.steps, 30);
	EXPECT_EQ(args.mode, Mode::FAST);
	EXPECT_EQ(args.strategies[0], "change");
	EXPECT_EQ(args.strategies[1], "random");
	EXPECT_EQ(args.strategies[2], "mimic");

	std::vector<const char *> argv2 = {"", "--steps="};
	EXPECT_THROW(ui.parse_args(2, const_cast<char **>(argv2.data())), std::invalid_argument);

	std::vector<const char *> argv3 = {"", "--steps30"};
	EXPECT_THROW(ui.parse_args(2, const_cast<char **>(argv3.data())), std::invalid_argument);

	std::vector<const char *> argv4 = {"", "--mode=test"};
	EXPECT_THROW(ui.parse_args(2, const_cast<char **>(argv4.data())), std::invalid_argument);

	std::vector<const char *> argv5 = {"", "--steps=ggagaa"};
	EXPECT_THROW(ui.parse_args(2, const_cast<char **>(argv5.data())), std::invalid_argument);

	std::vector<const char *> argv6 = {"", "--lol=2021"};
	EXPECT_THROW(ui.parse_args(2, const_cast<char **>(argv6.data())), std::invalid_argument);
}

TEST(prison, read_matrix_check)
{
	std::string path("/mnt/c/Users/sadri/20201_sadriev/prisoner/Google_tests/matrix_file");
	Matrix matrix = read_matrix(path);
	EXPECT_EQ(matrix[0][0], 1);
	EXPECT_EQ(matrix[0][1], 2);
	EXPECT_EQ(matrix[0][2], 3);

	EXPECT_EQ(matrix[7][0], 4);
	EXPECT_EQ(matrix[7][1], 5);
	EXPECT_EQ(matrix[7][2], 6);

	std::string blank;
	Matrix matrix2 = read_matrix(blank);
	EXPECT_EQ(matrix2[0][0], 7);
	EXPECT_EQ(matrix2[0][1], 7);
	EXPECT_EQ(matrix2[0][2], 7);

	EXPECT_EQ(matrix2[7][0], 1);
	EXPECT_EQ(matrix2[7][1], 1);
	EXPECT_EQ(matrix2[7][2], 1);

	std::string invalid_matrix_path("/mnt/c/Users/sadri/20201_sadriev/prisoner/Google_tests/invalid_matrix");
	EXPECT_THROW(read_matrix(invalid_matrix_path), std::invalid_argument);

	std::string invalid_path("matrix_file");
	EXPECT_THROW(read_matrix(invalid_path), std::invalid_argument);
}


TEST(prison, read_functions_check)
{
	std::string path("/mnt/c/Users/sadri/20201_sadriev/prisoner/Google_tests/read_functions");
	std::ifstream stream(path);
	std::size_t num = 20201;
	std::string first_line = "next line is a number!";
	std::string input_first = read_string(stream);
	if (!(first_line == input_first))
	{
		EXPECT_TRUE(false);
	}

	EXPECT_EQ(read_size_t(stream), num);
	read_string(stream);
	EXPECT_THROW(read_size_t(stream), std::invalid_argument);
}

TEST(prison, config_provider_check)
{
	std::string path("/mnt/c/Users/sadri/20201_sadriev/prisoner/configs");
	Provider::get_instance()->set_dir(path);
	std::string from_provider = Provider::get_instance()->get_dir();
	if (path != from_provider)
	{
		EXPECT_TRUE(false);
	}
}

TEST(prison, runs)
{
	std::vector<const char *> argv = {"blank", "election", "patient", "mimic", "--mode=fast", "--steps=30",
					  "--matrix=/mnt/c/Users/sadri/20201_sadriev/prisoner/Google_tests/matrix_file",
					  "--configs=/mnt/c/Users/sadri/20201_sadriev/prisoner/configs"};

	CLI ui;
	Args args = ui.parse_args((int)argv.size(), const_cast<char **>(argv.data()));
	Provider::get_instance()->set_dir(args.config_dir);
	Matrix input_matrix = read_matrix(args.matrix_file);
	std::vector<std::string> strategies2(args.strategies);

	Fast_runner runner1(input_matrix, args.strategies, args.steps);
	EXPECT_NO_THROW(runner1.run(ui));

	Tournament_runner runner2(input_matrix, strategies2, args.steps);
	EXPECT_NO_THROW(runner2.run(ui));

	std::vector<const char *> argv2 = {"blank", "random", "mimic", "not_strategy"};
	Args args2 = ui.parse_args((int)argv2.size(), const_cast<char **>(argv2.data()));

	std::vector<std::string> strategies3(args2.strategies);
	EXPECT_THROW(Fast_runner runner3(read_matrix(args2.matrix_file), args2.strategies, args2.steps), std::invalid_argument);

	Tournament_runner runner4(read_matrix(args2.matrix_file), strategies3, args2.steps);
	EXPECT_THROW(runner4.run(ui), std::invalid_argument);
}