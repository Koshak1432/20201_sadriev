#ifndef GAME_OF_LIFE_V2_0_RENDERAREA_H
#define GAME_OF_LIFE_V2_0_RENDERAREA_H

#include <QWidget>

class Field;

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
	QSize sizeHint() const override;

private:
	Field *field_ = nullptr;
	bool drawing = false;
	QPoint lastPoint;

	void drawLine(const QPoint &startPoint, const QPoint &endPoint, bool cellState);
};

#endif //GAME_OF_LIFE_V2_0_RENDERAREA_H
