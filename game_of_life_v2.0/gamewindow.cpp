#include "gamewindow.h"

#include <QLayout>
#include <QToolBar>
#include <QScrollArea>

#include "renderarea.h"

GameWindow::GameWindow() : game_()
{
	createToolBar();
	setCentralWidget(game_.getScrollArea());

	setWindowTitle("Game of life");
}

void GameWindow::createToolBar()
{
	auto *toolBar = addToolBar("Game Of Life");

	auto *playAction = new QAction("Play");
	auto *pauseAction = new QAction("Pause");
	auto *saveAction = new QAction("Save");
	auto *loadAction = new QAction("Load");
	auto *zoomInAction = new QAction("+");
	auto *zoomOutAction = new QAction("-");


	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(loadAction);
	toolBar->addAction(zoomInAction);
	toolBar->addAction(zoomOutAction);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
	connect(zoomInAction, &QAction::triggered, game_.getRenderArea(), &RenderArea::zoomIn);
	connect(zoomOutAction, &QAction::triggered, game_.getRenderArea(), &RenderArea::zoomOut);
//	connect(saveButton, &QAction::triggered, &game_, &Game::play);
//	connect(loadButton, &QAction::triggered, &game_, &Game::play);
}
