#ifndef GAME_OF_LIFE_V2_0_IFIELD_H
#define GAME_OF_LIFE_V2_0_IFIELD_H

class IField
{
public:
	virtual ~IField() = default;
	virtual void setCell(int x, int y, bool cell) noexcept = 0;
	[[nodiscard]] virtual bool getCell(int x, int y) const noexcept = 0;
	[[nodiscard]] virtual int getHeight() const noexcept = 0;
	[[nodiscard]] virtual int getWidth() const noexcept = 0;
};

#endif //GAME_OF_LIFE_V2_0_IFIELD_H
