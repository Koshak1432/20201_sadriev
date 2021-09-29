#include "gtest/gtest.h"
#include "../src/FlatMap.h"


TEST(FlatTest, ContainsTest)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	EXPECT_EQ(my_map.contains(str2), 1);
}

TEST(FlatTest, ClearTest)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.clear();
	EXPECT_EQ(my_map.contains(str1), 0);
	EXPECT_EQ(my_map.contains(str2), 0);
	EXPECT_EQ(my_map.empty(), 1);
	EXPECT_EQ(my_map.size(), 0);
}

TEST(FlatTest, ResizeTest)
{
	FlatMap<std::string, int> my_map(3);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "BATON";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	my_map.insert(str4, 4);
	EXPECT_EQ(my_map.size(), 4);
}

TEST(FlatTest, DoubleInsert)
{
	FlatMap<std::string, int> my_map(4);
	std::string str1 = "sanya";
	std::string str2 = "sanya";
	my_map.insert(str1, 1);
	EXPECT_EQ(my_map.insert(str2, 2), 0);
}

TEST(FlatTest, EqEq)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	my_map2.insert(str1, 1);
	my_map2.insert(str2, 2);
	my_map2.insert(str3, 3);
	EXPECT_TRUE(my_map == my_map2);
}

TEST(FlatTest, NEq)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	my_map2.insert(str1, 1);
	my_map2.insert(str2, 2);
	EXPECT_TRUE(my_map != my_map2);
}

TEST(FlatTest, opEq)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map2 = my_map;
	EXPECT_TRUE(my_map == my_map2);
}

TEST(FlatTest, CopyCtor)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	FlatMap<std::string, int> my_map2(my_map);
	EXPECT_TRUE(my_map == my_map2);
}