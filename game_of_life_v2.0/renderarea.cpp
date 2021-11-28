#include "renderarea.h"

#include <QPaintEvent>
#include <QPainter>
#include <cmath>

#include "engine.h"

constexpr int RECT_WIDTH = 50;
constexpr int RECT_HEIGHT = 50;
constexpr int BORDER_WIDTH = 4;
constexpr bool CELL_LIVE = true;
constexpr bool CELL_DEAD = false;
constexpr QColor COLOR_LIVE(57,255,20);
constexpr QColor COLOR_DEAD(255, 0, 0);

RenderArea::RenderArea(Field &field, QWidget *parent) : QWidget(parent), field_(&field)
{}

void RenderArea::paintEvent(QPaintEvent *event)
{
	QPainter painter = QPainter(this);
	QPen pen;
	//nPen.setBrush(QBrush()); //to print without border
	pen.setWidth(BORDER_WIDTH);
	pen.setJoinStyle(Qt::MiterJoin);
	painter.setPen(pen);
	for (int y = 0; y < field_->getHeight(); ++y)
	{
		for (int x = 0; x < field_->getWidth(); ++x)
		{
			(field_->getCell(x, y)) ? painter.setBrush(COLOR_LIVE) : painter.setBrush(COLOR_DEAD);
			painter.drawRect(x * RECT_WIDTH + BORDER_WIDTH / 2, y * RECT_HEIGHT + BORDER_WIDTH / 2, RECT_WIDTH, RECT_HEIGHT); //учитывать при обработке кликов сдвиг на 2
		}
	}
//	QPen mPen;
//	mPen.setWidth(0);
//	paint.setPen(mPen);
//	for (int y = 0; y < field_->getHeight(); ++y)
//	{
//		paint.drawLine(0, y * RECT_HEIGHT, static_cast<int>(field_->getWidth()) * RECT_WIDTH, y * RECT_HEIGHT);
//	}
//	for (int x = 0; x < field_->getWidth(); ++x)
//	{
//		paint.drawLine(x * RECT_WIDTH, 0, x * RECT_WIDTH, static_cast<int>(field_->getHeight()) * RECT_HEIGHT);
//	}
}

void RenderArea::mousePressEvent(QMouseEvent *event)
{
	if (Qt::LeftButton == event->button() || Qt::RightButton == event->button())
	{
		lastPoint = event->pos();
		drawing = true;
	}
}

void RenderArea::mouseMoveEvent(QMouseEvent *event)
{
	if (drawing)
	{
		if (event->buttons() & Qt::LeftButton)
		{
			drawLine(lastPoint, event->pos(), CELL_LIVE);
		}
		else if (event->buttons() & Qt::RightButton)
		{
			drawLine(lastPoint, event->pos(), CELL_DEAD);
		}
		lastPoint = event->pos();
	}
}

void RenderArea::mouseReleaseEvent(QMouseEvent *event)
{
	if (drawing)
	{
		if (Qt::LeftButton == event->button())
		{
			drawLine(lastPoint, event->pos(), CELL_LIVE);
		}
		else if (Qt::RightButton == event->button())
		{
			drawLine(lastPoint, event->pos(), CELL_DEAD);
		}
	}
	drawing = false;
}

void RenderArea::drawLine(const QPoint &startPoint, const QPoint &endPoint, bool cellState)
{
	int x0 = (startPoint.x() - (BORDER_WIDTH / 2)) / RECT_WIDTH;
	int x1 = (endPoint.x() - (BORDER_WIDTH / 2)) / RECT_WIDTH;
	int y0 = (startPoint.y() - (BORDER_WIDTH / 2)) / RECT_HEIGHT;
	int y1 = (endPoint.y() - (BORDER_WIDTH / 2)) / RECT_HEIGHT;

	int dx = std::abs(x0 - x1);
	int sx = (x0 < x1) ? 1 : -1;
	int dy = - std::abs(y0 - y1);
	int sy = (y0 < y1) ? 1 : -1;
	int err = dx + dy;
	while (true)
	{
		field_->setCell(x0, y0, cellState);
		if (x0 == x1 && y0 == y1)
		{
			break;
		}
		int e2 = 2 * err;
		if (e2 >= dy)
		{
			err +=dy;
			x0 += sx;
		}
		if (e2 <= dx)
		{
			err +=dx;
			y0 +=sy;
		}
	}
	update();
}



