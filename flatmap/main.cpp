#include <iostream>
#include <cassert>

#include "src/Array.h"
#include "src/FlatMap.h"

int main()
{
	std::string str1 = "sanya";
	std::string str2 = "Terentiy";
	std::string str3 = "Johan";
	std::string str4 = "John";
	std::string str5 = "Nipsey";
	std::string str6 = "Yo";
	FlatMap<std::string, int> my_map(5);
	my_map.insert(str1, 1);
	my_map.insert(str2, 2);
	my_map.print_flatmap();
	my_map.insert(str3, 3);
	my_map.print_flatmap();
	my_map.insert(str4, 4);

	cout << str1 << " " << my_map.contains(str1) << endl;
	cout << str2 << " " << my_map.contains(str2) << endl;
	cout << str3 << " " << my_map.contains(str3) << endl;
	cout << str4 << " " << my_map.contains(str4) << endl;
	cout << str5 << " " << my_map.contains(str5) << endl;
	cout << str6 << " " << my_map.contains(str6) << endl;
	my_map.insert(str5, 5);
	my_map.insert(str6, 6);
	my_map.print_flatmap();
	cout << str5 << " " << my_map.contains(str5) << endl;
	cout << str6 << " " << my_map.contains(str6) << endl;



	return 0;
}
