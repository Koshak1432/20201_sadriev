#include "gamewindow.h"

#include <QLayout>
#include <QToolBar>
#include <QScrollArea>
#include <QFileDialog>
#include <QMessageBox>

#include "renderarea.h"
#include "io.h"

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
	auto *openAction = new QAction("Open");

	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(openAction);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
	connect(openAction, &QAction::triggered, this, &GameWindow::open);
//	connect(saveButton, &QAction::triggered, &game_, &Game::play);
}

void GameWindow::open()
{
	QString fileName = QFileDialog::getOpenFileName(this);
	if (!fileName.isEmpty())
	{
		loadFile(fileName);
	}
}

void GameWindow::loadFile(const QString &fileName)
{
	QFile file(fileName);
	if (!file.open(QFile::ReadOnly | QFile::Text))
	{
		QMessageBox::warning(this, "Application", tr("Cannot read file %1:\n%2.") .arg(QDir::toNativeSeparators(fileName), file.errorString()));
	}
	game_ = std::move(Game(readState(&file)));
	update();
}
