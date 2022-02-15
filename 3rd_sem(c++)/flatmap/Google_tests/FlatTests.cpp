#include "gtest/gtest.h"
#include "../src/FlatMap.h"


TEST(FlatTest, ContainsTest)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.contains(str2));
	EXPECT_TRUE(my_map.contains(str1));
	EXPECT_EQ(my_map.size(), 2);
}

TEST(FlatTest, ClearTest)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	my_map.clear();
	EXPECT_FALSE(my_map.contains(str1));
	EXPECT_FALSE(my_map.contains(str2));
	EXPECT_TRUE(my_map.empty());
	EXPECT_FALSE(my_map.erase(str1));
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert (str2, 2));
	EXPECT_EQ(my_map.size(), 2);
}

TEST(FlatTest, ResizeTest)
{
	FlatMap<std::string, int> my_map(2);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "BATON";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.insert(str3, 3));
	EXPECT_TRUE(my_map.insert(str4, 4));
	EXPECT_EQ(my_map.size(), 4);
}

TEST(FlatTest, DoubleInsert)
{
	FlatMap<std::string, int> my_map(4);
	std::string str1 = "sanya";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_FALSE(my_map.insert(str1, 2));
	EXPECT_EQ(my_map.size(), 1);
}

TEST(FlatTest, opEqEQ)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "Paul George";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.insert(str3, 3));
	EXPECT_TRUE(my_map2.insert(str1, 1));
	EXPECT_TRUE(my_map2.insert(str2, 2));
	EXPECT_TRUE(my_map2.insert(str3, 3));
	EXPECT_TRUE(my_map == my_map2);
	EXPECT_TRUE(my_map.insert(str4, 4));
	EXPECT_TRUE(my_map.erase(str1));
	EXPECT_FALSE(my_map == my_map2);
	EXPECT_EQ(my_map.size(), 3);
	EXPECT_EQ(my_map2.size(), 3);
}

TEST(FlatTest, opNEq)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.insert(str3, 3));
	EXPECT_TRUE(my_map2.insert(str1, 1));
	EXPECT_TRUE(my_map2.insert(str2, 2));
	EXPECT_TRUE(my_map != my_map2);
	EXPECT_EQ(my_map.size(), 3);
	EXPECT_EQ(my_map2.size(),2);
}

TEST(FlatTest, opEq)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	my_map2 = my_map;
	EXPECT_TRUE(my_map == my_map2);
	EXPECT_EQ(my_map.size(), 2);
}

TEST(FlatTest, CopyCtor)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	FlatMap<std::string, int> my_map2(my_map);
	EXPECT_TRUE(my_map == my_map2);
	EXPECT_EQ(my_map.size(), 2);
}

TEST(FlatTest, Erase)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map2.insert(str2, 2));
	EXPECT_TRUE(my_map.erase(str1));
	EXPECT_FALSE(my_map.erase(str1));
	EXPECT_TRUE(my_map == my_map2);
	EXPECT_EQ(my_map.size(), 1);
}

TEST(FlatTest, at)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "KEKa";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_EQ(my_map.at(str1), 1);
	EXPECT_THROW(my_map.at(str3), std::out_of_range);

	const auto &r_map = my_map;
	EXPECT_EQ(r_map.at(str1), 1);
	EXPECT_THROW(r_map.at(str3), std::out_of_range);
	EXPECT_EQ(my_map.size(), 2);
}

TEST(FlatTest, opBrackets)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "KEKa";
	std::string str4 = "PRISONER №993";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_EQ(my_map[str1], 1);
	EXPECT_EQ(my_map[str3], int());
	EXPECT_EQ(my_map.size(), 3);

	my_map[str3] = 3;
	my_map[str2] = 22;
	my_map[str1] = 11111;
	my_map[str4] = 4;
	EXPECT_EQ(my_map[str3], 3);
	EXPECT_EQ(my_map[str2], 22);
	EXPECT_EQ(my_map[str1], 11111);
	EXPECT_EQ(my_map[str4], 4);
}

TEST(FlatTest, swap)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.insert(str3, 3));
	EXPECT_TRUE(my_map2.insert(str1, 1));
	EXPECT_TRUE(my_map2.insert(str2, 2));

	FlatMap<std::string, int> test_map1(my_map);
	FlatMap<std::string, int> test_map2(my_map2);
	my_map.swap(my_map2);
	EXPECT_TRUE(test_map1 == my_map2);
	EXPECT_TRUE(test_map2 == my_map);
	EXPECT_EQ(my_map.size(), 2);
	EXPECT_EQ(my_map2.size(), 3);
}

TEST(FlatTest, moveOpMoveCtor)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "Paul George";
	std::string str5 = "Klay Thompson";

	EXPECT_TRUE(my_map.insert(str1, 1));
	EXPECT_TRUE(my_map.insert(str2, 2));
	EXPECT_TRUE(my_map.insert(str3, 3));
	FlatMap<std::string, int> tmp_map = my_map;
	FlatMap<std::string, int> my_map2(std::move(my_map));
	EXPECT_TRUE(my_map.empty());
	EXPECT_TRUE(tmp_map == my_map2);
	EXPECT_EQ(tmp_map.size(), 3);

	FlatMap<std::string, int> my_map3(5);
	my_map3 = std::move(my_map2);
	EXPECT_TRUE(my_map2.empty());
	EXPECT_TRUE(my_map3 == tmp_map);
}