#include <QtWidgets>

#include "gamewindow.h"

int main(int argc, char *argv[])
{
	QApplication app(argc, argv);
	GameWindow window;
	window.setWindowState(Qt::WindowMaximized);
	window.show();
	return app.exec();

}
