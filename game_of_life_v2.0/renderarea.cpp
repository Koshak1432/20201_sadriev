#include "renderarea.h"

#include <QPaintEvent>
#include <QPainter>

#include "engine.h"

constexpr int RECT_WIDTH = 50;
constexpr int RECT_HEIGHT = 50;
constexpr int BORDER_WIDTH = 4;
constexpr QColor COLOR_LIVE(57,255,20);
constexpr QColor COLOR_DEAD(255, 0, 0);

RenderArea::RenderArea(Field &field, QWidget *parent) : QWidget(parent), field_(&field)
{}

void RenderArea::paintEvent(QPaintEvent *event)
{
	QPainter paint = QPainter(this);
	QPen pen;
	//nPen.setBrush(QBrush()); //to print without border
	pen.setWidth(BORDER_WIDTH);
	pen.setJoinStyle(Qt::MiterJoin);
	paint.setPen(pen);
	for (int y = 0; y < field_->getHeight(); ++y)
	{
		for (int x = 0; x < field_->getWidth(); ++x)
		{
			(field_->getCell(x, y)) ? paint.setBrush(COLOR_LIVE) : paint.setBrush(COLOR_DEAD);
			paint.drawRect(x * RECT_WIDTH + BORDER_WIDTH / 2, y * RECT_HEIGHT + BORDER_WIDTH / 2, RECT_WIDTH, RECT_HEIGHT); //учитывать при обработке кликов сдвиг на 2
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
	if (Qt::LeftButton == event->button())
	{
		lastPoint = event->pos();
		drawing = true;
	}
}

void RenderArea::mouseMoveEvent(QMouseEvent *event)
{
	if ((event->buttons() & Qt::LeftButton) && drawing) //mb button == leftButton
	{
		drawLineTo(event->pos());
	}
}

void RenderArea::mouseReleaseEvent(QMouseEvent *event)
{
	if (Qt::LeftButton == event->button() && drawing)
	{
		drawLineTo(event->pos());
		drawing = false;
	}
}

void RenderArea::drawLineTo(const QPoint &endPoint)
{

}



