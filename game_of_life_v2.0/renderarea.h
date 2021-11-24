#ifndef GAME_OF_LIFE_V2_0_RENDERAREA_H
#define GAME_OF_LIFE_V2_0_RENDERAREA_H

#include <QWidget>

#include "engine.h"

class RenderArea :public QWidget
{
	Q_OBJECT
public:
	explicit RenderArea(Field &field, QWidget *parent = nullptr);

protected:
	void paintEvent(QPaintEvent *event) override;

private:
	Field *field_ = nullptr;
};

#endif //GAME_OF_LIFE_V2_0_RENDERAREA_H
