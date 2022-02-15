#include "renderarea.h"

#include <QPainter>

#include "engine.h"

namespace
{
	constexpr int DEFAULT_RECT_WIDTH = 20;
	constexpr int DEFAULT_RECT_HEIGHT = 20;
	constexpr int BORDER_WIDTH = 1;
	constexpr bool CELL_LIVE = true;
	constexpr bool CELL_DEAD = false;
	constexpr QColor COLOR_LIVE(57,255,20);
	constexpr QColor COLOR_DEAD(255, 0, 0);
	constexpr double SCALE_LOWER_BORDER = 0.1;
	constexpr double SCALE_UPPER_BORDER = 5.0;
	constexpr double ZOOM_IN_FACTOR = 1.25;
	constexpr double ZOOM_OUT_FACTOR = 1 / ZOOM_IN_FACTOR;
}

RenderArea::RenderArea(IField &field, QWidget *parent) : QWidget(parent), field_(&field)
{}

void RenderArea::paintEvent(QPaintEvent *event)
{
	QPainter painter = QPainter(this);
	QPen pen;
	pen.setWidth(BORDER_WIDTH);
	pen.setJoinStyle(Qt::MiterJoin);
	painter.setPen(pen);

	for (int y = 0; y < field_->getHeight(); ++y)
	{
		for (int x = 0; x < field_->getWidth(); ++x)
		{
			(field_->getCell(x, y)) ? painter.setBrush(COLOR_LIVE) : painter.setBrush(COLOR_DEAD);
			painter.drawRect(x * getScaledRectWidth() + BORDER_WIDTH / 2, y * getScaledRectHeight() + BORDER_WIDTH / 2, getScaledRectWidth(), getScaledRectHeight());
		}
	}
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
		if (this->rect().contains(event->pos(), true))
		{
			if (event->buttons() & Qt::LeftButton)
			{
				drawLine(lastPoint, event->pos(), CELL_LIVE);
			}
			else if (event->buttons() & Qt::RightButton)
			{
				drawLine(lastPoint, event->pos(), CELL_DEAD);
			}

		}
		lastPoint = event->pos();
	}
}

void RenderArea::mouseReleaseEvent(QMouseEvent *event)
{
	if (drawing && this->rect().contains(event->pos(), true))
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
	int x0 = (startPoint.x() - (BORDER_WIDTH / 2)) / getScaledRectWidth();
	int x1 = (endPoint.x() - (BORDER_WIDTH / 2)) / getScaledRectWidth();
	int y0 = (startPoint.y() - (BORDER_WIDTH / 2)) / getScaledRectHeight();
	int y1 = (endPoint.y() - (BORDER_WIDTH / 2)) / getScaledRectHeight();

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
			err += dy;
			x0 += sx;
		}
		if (e2 <= dx)
		{
			err += dx;
			y0 += sy;
		}
	}
	update();
}

QSize RenderArea::sizeHint() const
{
	return {field_->getWidth() * getScaledRectWidth(), field_->getHeight() * getScaledRectHeight()};
}

void RenderArea::zoomIn()
{
	scaleArea(ZOOM_IN_FACTOR);
}

void RenderArea::zoomOut()
{
	scaleArea(ZOOM_OUT_FACTOR);
}

void RenderArea::scaleArea(double scaleFactor)
{
	double attemptedScale = scaleFactor_ * scaleFactor;

	if (attemptedScale < SCALE_LOWER_BORDER || attemptedScale > SCALE_UPPER_BORDER)
	{
		return;
	}
	drawing = false;
	scaleFactor_ = attemptedScale;
	resize(sizeHint());
}

void RenderArea::wheelEvent(QWheelEvent *event)
{
	(event->angleDelta().y() > 0) ? zoomIn() : zoomOut();
}

int RenderArea::getScaledRectWidth() const noexcept
{
	return int(scaleFactor_ * DEFAULT_RECT_WIDTH);
}

int RenderArea::getScaledRectHeight() const noexcept
{
	return int(scaleFactor_ * DEFAULT_RECT_HEIGHT);
}

