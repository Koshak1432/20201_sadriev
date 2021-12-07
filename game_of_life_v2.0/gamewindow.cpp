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

	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(loadAction);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
//	connect(saveButton, &QAction::triggered, &game_, &Game::play);
//	connect(loadButton, &QAction::triggered, &game_, &Game::play);
}
