#ifndef PRISONER_STRATEGY_FACTORY_H
#define PRISONER_STRATEGY_FACTORY_H

#include "factory.h"
#include "strategy.h"

using Strategy_factory = Factory<Strategy, std::string, std::function<std::unique_ptr<Strategy>()>>;

#endif //PRISONER_STRATEGY_FACTORY_H
