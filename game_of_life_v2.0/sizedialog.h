#ifndef GAME_OF_LIFE_V2_0_SIZEDIALOG_H
#define GAME_OF_LIFE_V2_0_SIZEDIALOG_H

#include <QDialog>
class QSpinBox;

class SizeDialog : public QDialog
{
	Q_OBJECT

public:

	explicit SizeDialog(const QPoint &size, QWidget *parent = nullptr);
	void changeSizeSpinBoxes(QPoint size);

signals:
	void widthChanged(int width);
	void heightChanged(int height);

private:
	QSpinBox *widthSpinBox;
	QSpinBox *heightSpinBox;

	enum SizeType
	{
		WIDTH,
		HEIGHT
	};
	QSpinBox * createSpinBox(int value, SizeType type);

};



#endif //GAME_OF_LIFE_V2_0_SIZEDIALOG_H
