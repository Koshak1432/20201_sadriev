#include "renderarea.h"

#include <QPaintEvent>
#include <QPainter>

#include "engine.h"

constexpr int DEFAULT_RECT_WIDTH = 20;
constexpr int DEFAULT_RECT_HEIGHT = 20;
constexpr int BORDER_WIDTH = 1;
constexpr bool CELL_LIVE = true;
constexpr bool CELL_DEAD = false;
constexpr QColor COLOR_LIVE(57,255,20);
constexpr QColor COLOR_DEAD(255, 0, 0);
constexpr double SCALE_LOWER_BORDER = 0.33;
constexpr double SCALE_UPPER_BORDER = 4.0;
constexpr double ZOOM_IN_FACTOR = 1.25;
constexpr double ZOOM_OUT_FACTOR = 1 / ZOOM_IN_FACTOR;



RenderArea::RenderArea(Field &field, QWidget *parent) : QWidget(parent), field_(&field)
{}

void RenderArea::paintEvent(QPaintEvent *event)
{
	QPainter painter = QPainter(this);
	QPen pen;
	pen.setWidth(BORDER_WIDTH);
	pen.setJoinStyle(Qt::MiterJoin);
	painter.setPen(pen);
	int scaledRectWidth = static_cast<int>(DEFAULT_RECT_WIDTH * scaleFactor_);
	int scaledRectHeight = static_cast<int>(DEFAULT_RECT_HEIGHT * scaleFactor_);

	for (int y = 0; y < field_->getHeight(); ++y)
	{
		for (int x = 0; x < field_->getWidth(); ++x)
		{
			(field_->getCell(x, y)) ? painter.setBrush(COLOR_LIVE) : painter.setBrush(COLOR_DEAD);
			//учитывать при обработке кликов сдвиг на border / width
			painter.drawRect(x * scaledRectWidth + BORDER_WIDTH / 2, y * scaledRectHeight + BORDER_WIDTH / 2, scaledRectWidth, scaledRectHeight);
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
	int scaledRectWidth = static_cast<int>(DEFAULT_RECT_WIDTH * scaleFactor_);
	int scaledRectHeight = static_cast<int>(DEFAULT_RECT_HEIGHT * scaleFactor_);

	int x0 = (startPoint.x() - (BORDER_WIDTH / 2)) / scaledRectWidth;
	int x1 = (endPoint.x() - (BORDER_WIDTH / 2)) / scaledRectWidth;
	int y0 = (startPoint.y() - (BORDER_WIDTH / 2)) / scaledRectHeight;
	int y1 = (endPoint.y() - (BORDER_WIDTH / 2)) / scaledRectHeight;

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

QSize RenderArea::sizeHint() const
{
	return {static_cast<int>(DEFAULT_WIDTH * DEFAULT_RECT_WIDTH * scaleFactor_), static_cast<int>(DEFAULT_HEIGHT * DEFAULT_RECT_HEIGHT * scaleFactor_)}; //return QSize(...)
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
	scaleFactor_ *= scaleFactor;
	if (scaleFactor_ < SCALE_LOWER_BORDER || scaleFactor_ > SCALE_UPPER_BORDER)
	{
		scaleFactor_ = DEFAULT_SCALE_FACTOR;
	}
	resize(sizeHint());
}

