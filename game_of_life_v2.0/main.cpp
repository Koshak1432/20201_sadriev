#include <QtWidgets>

#include "engine.h"
//#include "mainwindow.h"
#include "renderarea.h"


int main(int argc, char *argv[])
{
	QApplication app(argc, argv);
	State state{};
	Field field = state.getField();
	RenderArea area(field);
	area.show();
//	state.play();
//	Window window(state.getField());
//	window.show();

	return app.exec();
}
