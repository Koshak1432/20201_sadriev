#include "io.h"

#include <iostream>

#include <QString>
#include <QVector>
#include <QIODevice>
#include <QDebug>

#include "engine.h"

constexpr QChar LIVE_CELL = 'o';
constexpr QChar DEAD_CELL = 'b';
constexpr QChar END_OF_LINE = '$';
constexpr QChar RLE_END = '!';
constexpr int CHARS_NUMBER = 128;
constexpr int OFFSET_FIELD = 30;
constexpr int OFFSET_COORD = 10;

enum class Expectations
{
	SPACE = CHARS_NUMBER,
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
	std::vector<Expectations> expect{Expectations {'x'}, Expectations::SPACE, Expectations{'='}, Expectations::SPACE,
								  Expectations::NUMBER, Expectations{','}, Expectations::SPACE, Expectations{'y'},
								  Expectations::SPACE, Expectations{'='}, Expectations::SPACE, Expectations::NUMBER,
								  Expectations{','}, Expectations::SPACE, Expectations{'r'}, Expectations{'u'},
								  Expectations{'l'}, Expectations{'e'}, Expectations::SPACE, Expectations{'='},
								  Expectations::SPACE, Expectations{'B'}, Expectations::NUMBER, Expectations{'/'},
								  Expectations{'S'}, Expectations::NUMBER, Expectations::SPACE, Expectations{'\n'}};

	constexpr int numNumbers = 4;
	QStringList stringNumbers{};
	stringNumbers.reserve(numNumbers);

	for (auto action : expect)
	{
		if (static_cast<int>(action) < static_cast<int>(Expectations::SPACE))
		{
			expectChar(device, static_cast<char>(action));
			device->skip(sizeof(char));
			continue;
		}
		switch (action)
		{
			case Expectations::SPACE:
			{
				skipWhiteSpaces(device);
				break;
			}
			case Expectations::NUMBER:
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
	State state(std::move(rules), stringNumbers[0].toInt(&ok) + OFFSET_FIELD, stringNumbers[1].toInt(&ok) + OFFSET_FIELD);
	if (!ok)
	{
		throw std::invalid_argument("can't convert width and height from header info");
	}
	return state;
}

static void readRLE(QIODevice *device, State &state, int &x, int &y, bool &end)
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
		skipWhiteSpaces(device);
		if (!device->getChar(&tag))
		{
			throw std::invalid_argument("can't read rle");
		}
		if ('$' == tag)
		{
			x = 0;
			y += runCount;
		}
		else if ('\n' == tag)
		{
			if (!stringRunCount.isEmpty())
			{
				throw std::invalid_argument("new line after run count is forbidden");
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
		else //RLE readers that cannot handle more than two states should treat all letters other than b as equivalent to o.
		{
			for (int i = 0; i < runCount; ++i)
			{
				if (x < state.getWidth() && y < state.getHeight())
				{
					state.getCurrent().setCell(x + OFFSET_COORD, y + OFFSET_COORD, 'b' != tag);
					++x;
				}
				else
				{
					throw std::invalid_argument("invalid info in rle");
				}
			}
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
			case ' ':
			{
				throw std::invalid_argument("space in the beginning of line is forbidden");
			}
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
				readRLE(device, state, x, y, end);
				if (end)
				{
					return state;
				}
			}
		}
	}
}

static void writeRulesIdx(QTextStream &out, const std::vector<bool> &rule)
{
	for (int i = 0; i < rule.size(); ++i)
	{
		if (rule[i])
		{
			out << i;
		}
	}
}

static void writeHeader(QTextStream &out, const State &state)
{
	Rules rules(state.getRules());

	out << "x = " << state.getWidth() << ", y = " << state.getHeight() << ", rule = B";
	writeRulesIdx(out, rules.birth_);
	out << "/S";
	writeRulesIdx(out, rules.sustain_);
	out << "\n";
}

static QString getRowData(Field &field, int row)
{
	QString rowString;
	rowString.reserve(field.getWidth());
	for (int col = 0; col < field.getWidth(); ++col)
	{
		field.getCell(col, row) ? rowString.append(LIVE_CELL) : rowString.append(DEAD_CELL);
	}
	return rowString;
}

static QString getEncodedString(QString data)
{
	if (data.isEmpty())
	{
		return {};
	}
	QChar prevChar = data[0];
	ulong count = 1;
	QString encoding;
	encoding.reserve(data.size());

	for (qsizetype i = 1; i < data.size(); ++i)
	{
		QChar currentChar = data[i];
		if (currentChar != prevChar)
		{
			if (count > 1)
			{
				encoding += QString::number(count);
			}
			encoding += prevChar;
			prevChar = currentChar;
			count = 1;
		}
		else
		{
			++count;
		}
	}
	if (DEAD_CELL != prevChar)
	{
		if (count > 1)
		{
			encoding += QString::number(count);
		}
		encoding += prevChar;
	}
	return encoding;
}

static void writeRLE(QTextStream &out, Field &field)
{
	for (int row = 0; row < field.getHeight(); ++row)
	{
		out << getEncodedString(getRowData(field, row)) + END_OF_LINE + '\n';
	}
	out << RLE_END;
}

void saveToFile(QIODevice *device, State &state)
{
	QTextStream out(device);
	writeHeader(out, state);
	writeRLE(out, state.getCurrent());
}