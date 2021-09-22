#include "gtest/gtest.h"
#include "../src/FlatMap.h"


TEST(FlatTest, ContsinsTest)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	EXPECT_EQ(my_map.contains(str2), 1);
}
