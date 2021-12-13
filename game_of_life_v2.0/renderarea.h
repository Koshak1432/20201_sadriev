#ifndef GAME_OF_LIFE_V2_0_RENDERAREA_H
#define GAME_OF_LIFE_V2_0_RENDERAREA_H

#include <QWidget>
#include <QWheelEvent>

class Field;

namespace
{
	constexpr double DEFAULT_SCALE_FACTOR = 1.0;
}


class RenderArea :public QWidget
{
	Q_OBJECT
public:
	explicit RenderArea(Field &field, QWidget *parent = nullptr);
	~RenderArea() override = default;

protected:
	void paintEvent(QPaintEvent *event) override;
	void mousePressEvent(QMouseEvent *event) override;
	void mouseMoveEvent(QMouseEvent *event) override;
	void mouseReleaseEvent(QMouseEvent *event) override;
	void wheelEvent(QWheelEvent *event) override;
	[[nodiscard]] QSize sizeHint() const override;

private slots:
	void zoomIn();
	void zoomOut();

private:
	Field *field_ = nullptr;
	bool drawing = false;
	QPoint lastPoint;
	double scaleFactor_ = DEFAULT_SCALE_FACTOR;

	void drawLine(const QPoint &startPoint, const QPoint &endPoint, bool cellState);
	[[nodiscard]] int getScaledRectWidth() const noexcept;
	[[nodiscard]] int getScaledRectHeight() const noexcept;
	void scaleArea(double scaleFactor = DEFAULT_SCALE_FACTOR);
};

#endif //GAME_OF_LIFE_V2_0_RENDERAREA_H
