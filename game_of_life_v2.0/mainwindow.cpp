#include "mainwindow.h"

#include "renderarea.h"
#include "engine.h"

Window::Window(Field field) : renderArea(new RenderArea(field))
{
	setWindowTitle(tr("Game of life"));
}

