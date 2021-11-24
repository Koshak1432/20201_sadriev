#include "renderarea.h"

#include <QPaintEvent>
#include <QPainter>

constexpr int RECT_WIDTH = 10;
constexpr int RECT_HEIGHT = 10;
constexpr QColor COLOR_LIVE(57,255,20);
constexpr QColor COLOR_DEAD(255, 0, 0);

RenderArea::RenderArea(Field &field, QWidget *parent) : QWidget(parent), field_(&field)
{}

void RenderArea::paintEvent(QPaintEvent *event)  //make width and height int
{
	QPainter *paint = new QPainter(this);
	for (int y = 0; y < field_->get_height(); ++y)
	{
		for (int x = 0; x <field_->get_width(); ++x)
		{
			(field_->get_cell(x, y)) ? paint->setBrush(COLOR_LIVE) : paint->setBrush(COLOR_DEAD);
			paint->drawRect(x * RECT_WIDTH, y * RECT_HEIGHT, RECT_WIDTH, RECT_HEIGHT);
		}
	}
	QWidget::paintEvent(event);
}
