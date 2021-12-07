#ifndef GAME_OF_LIFE_V2_0_RENDERAREA_H
#define GAME_OF_LIFE_V2_0_RENDERAREA_H

#include <QWidget>

class Field;

constexpr double DEFAULT_SCALE_FACTOR = 1.0;

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
	[[nodiscard]] QSize sizeHint() const override;

public slots:
	void zoomIn();
	void zoomOut();

private:
	Field *field_ = nullptr;
	bool drawing = false;
	QPoint lastPoint;
	double scaleFactor_ = DEFAULT_SCALE_FACTOR;
	void drawLine(const QPoint &startPoint, const QPoint &endPoint, bool cellState);
	void scaleArea(double scaleFactor = DEFAULT_SCALE_FACTOR);
};

#endif //GAME_OF_LIFE_V2_0_RENDERAREA_H
