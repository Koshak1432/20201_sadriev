#include "io.h"

#include <iostream>

#include <QString>
#include <QVector>
#include <QIODevice>
#include <QDebug>

#include "engine.h"

enum class Expecting
{
	SPACE = 128,
	NUMBER,
};

static void expectChar(QIODevice *device, char expectedChar)
{
	if (expectedChar != device->peek(sizeof(expectedChar)).at(0))
	{
		throw std::invalid_argument("Invalid data in file (expectChar)");
	}
}

static bool isDigit(QIODevice *device)
{
	return (0 != std::isdigit(device->peek(sizeof(char)).at(0)));
}

static QString readNumber(QIODevice *device)
{
	QString str;
	char digit;
	while(isDigit(device))
	{
		if (!device->getChar(&digit))
		{
			throw std::invalid_argument("Invalid data in file (readNumber)");
		}
		str.append(digit);
	}
	return str;
}

static void skipWhiteSpaces(QIODevice *device)
{
	char ch{};
	if (sizeof(char) != device->peek(&ch, sizeof(char)))
	{
		throw std::invalid_argument("error in skip spaces");
	}
	while (' ' == ch)
	{
		device->skip(sizeof(char));
		if (sizeof(char) != device->peek(&ch, sizeof(char)))
		{
			throw std::invalid_argument("error in skip spaces");
		}
	}
}

static std::vector<bool> readRule(const QString &numInString)
{
	std::vector<bool> ruleVec(9, false);
	for (auto digit : numInString)
	{
		int idx = int(digit.toLatin1()) - '0';
		ruleVec[idx] = true;
	}
	return ruleVec;
}

static State readHeader(QIODevice *device)
{
	std::vector<Expecting> expect{Expecting {'x'}, Expecting::SPACE, Expecting{'='}, Expecting::SPACE,
								  Expecting::NUMBER, Expecting{','}, Expecting::SPACE, Expecting{'y'},
								  Expecting::SPACE, Expecting{'='}, Expecting::SPACE, Expecting::NUMBER,
								  Expecting{','}, Expecting::SPACE, Expecting{'r'}, Expecting{'u'}, Expecting{'l'}, Expecting{'e'},
								  Expecting::SPACE, Expecting{'='}, Expecting::SPACE, Expecting{'B'}, Expecting::NUMBER, Expecting{'/'},
								  Expecting{'S'}, Expecting::NUMBER, Expecting::SPACE, Expecting{'\n'}};

	constexpr int numNumbers = 4;
	QStringList stringNumbers{};
	stringNumbers.reserve(numNumbers);

	for (auto action : expect)
	{
		if (static_cast<int>(action) < 128)
		{
			expectChar(device, static_cast<char>(action));
			device->skip(sizeof(char));
			continue;
		}
		switch (action)
		{
			case Expecting::SPACE:
			{
				skipWhiteSpaces(device);
				break;
			}
			case Expecting::NUMBER:
			{
				stringNumbers.append(readNumber(device));
				break;
			}
			default:
			{
				assert(false);
			}
		}
	}

	Rules rules(readRule(stringNumbers[2]), readRule(stringNumbers[3]));

	bool ok = true;
	State state(std::move(rules), stringNumbers[0].toInt(&ok), stringNumbers[1].toInt(&ok));
	if (!ok)
	{
		throw std::invalid_argument("can't convert width and height from header info");
	}
	return state;
}

static void readRle(QIODevice *device, State &state, int &x, int &y, bool &end)
{
	while (true)
	{
		int runCount = 0;
		QString stringRunCount;
		char tag{};
		bool ok = true;

		stringRunCount = readNumber(device);
		if (stringRunCount.isEmpty())
		{
			runCount = 1;
		}
		else
		{
			runCount = stringRunCount.toInt(&ok);
			if (!ok)
			{
				throw std::invalid_argument("can't read rle");
			}
		}
		if (!device->getChar(&tag))
		{
			throw std::invalid_argument("can't read rle");
		}
		if ('b' == tag || 'o' == tag)
		{
			for (int i = 0; i < runCount; ++i)
			{
				if (x < state.getWidth() && y < state.getHeight())
				{
					state.getCurrent().setCell(x, y, 'o' == tag);
					++x;
				}
				else
				{
					throw std::invalid_argument("invalid info in rle");
				}
			}
		}
		else if ('$' == tag)
		{
			x = 0;
			++y;
		}
		else if ('\n' == tag)
		{
			if (!stringRunCount.isEmpty())
			{
				throw std::invalid_argument("invalid info in rle");
			}
			else
			{
				return;
			}
		}
		else if ('!' == tag)
		{
			end = true;
			return;
		}
		else
		{
			throw std::invalid_argument("can't read rle");
		}
	}
}

State readState(QIODevice *device)
{
	char ch{};
	State state;
	int x = 0;
	int y = 0;

	while (true)
	{
		if (sizeof(ch) != device->peek(&ch, sizeof(ch)))
		{
			throw std::invalid_argument("can't peek char");
		}
		switch (ch)
		{
			case '#':
			{
				device->readLine();
				break;
			}
			case 'x':
			{
				state = std::move(readHeader(device));
				break;
			}
			default:
			{
				bool end = false;
				readRle(device, state, x, y, end);
				if (end)
				{
					return state;
				}
			}
		}
	}
}