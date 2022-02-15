#ifndef PRISONER_MOCK_PRISON_H
#define PRISONER_MOCK_PRISON_H

#include "gmock/gmock.h"
#include "../game.h"
#include "../strategy.h"
#include "../strategies/change.h"

class MockStrategy : public Strategy
{
public:
	MOCK_METHOD(void, make_choice, (), (override));
	MOCK_METHOD(Choice, get_choice, (), (const, noexcept, override));
	MOCK_METHOD(void, handle_result, (const Result &res), (override));
};

#endif //PRISONER_MOCK_PRISON_H
