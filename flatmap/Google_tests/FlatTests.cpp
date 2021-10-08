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
	FlatMap<std::string, int> my_map(2);
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

TEST(FlatTest, opEqEQ)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "Paul George";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	my_map2.insert(str1, 1);
	my_map2.insert(str2, 2);
	my_map2.insert(str3, 3);
	EXPECT_TRUE(my_map == my_map2);
	my_map.insert(str4, 4);
	my_map.erase(str1);
	EXPECT_FALSE(my_map == my_map2);
}

TEST(FlatTest, opNEq)
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
	FlatMap<std::string, int> my_map3(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "KEKA";
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

TEST(FlatTest, Erase)
{
	FlatMap<std::string, int> my_map(5);
	FlatMap<std::string, int> my_map2(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map2.insert(str2, 2);
	EXPECT_TRUE(my_map.erase(str1));
	EXPECT_FALSE(my_map.erase(str1));
	EXPECT_TRUE(my_map == my_map2);
}

TEST(FlatTest, at)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "KEKa";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	EXPECT_EQ(my_map.at(str1), 1);
	EXPECT_THROW(my_map.at(str3), std::out_of_range);

	const auto &r_map = my_map;
	EXPECT_EQ(r_map.at(str1), 1);
	EXPECT_THROW(r_map.at(str3), std::out_of_range);
}

TEST(FlatTest, opBrackets)
{
	FlatMap<std::string, int> my_map(5);
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "KEKa";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	EXPECT_EQ(my_map[str1], 1);
	EXPECT_EQ(my_map[str3], 0);
}

TEST(FlatTest, swap)
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

	FlatMap<std::string, int> test_map1(my_map);
	FlatMap<std::string, int> test_map2(my_map2);
	my_map.swap(my_map2);
	EXPECT_TRUE(test_map1 == my_map2);
	EXPECT_TRUE(test_map2 == my_map);

}

TEST(FlatTest, moveOpMoveCtor)
{
	FlatMap<std::string, int> my_map(5);

	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "a";
	std::string str4 = "Paul George";
	std::string str5 = "Klay Thompson";
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.insert(str3, 3);
	FlatMap<std::string, int> tmp_map = my_map;
	FlatMap<std::string, int> my_map2(std::move(my_map));
	EXPECT_TRUE(my_map.empty());
	EXPECT_TRUE(tmp_map == my_map2);

	tmp_map = my_map2;
	FlatMap<std::string, int> my_map3(5);
	my_map3 = std::move(my_map2);
	EXPECT_TRUE(my_map2.empty());
	EXPECT_TRUE(my_map3 == tmp_map);
}