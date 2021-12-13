#include "gamewindow.h"

#include <QLayout>
#include <QToolBar>
#include <QLabel>
#include <QScrollArea>
#include <QFileDialog>
#include <QMessageBox>
#include <QSpinBox>

#include "renderarea.h"
#include "io.h"

namespace
{
	constexpr int MAX_SPEED = 100;
	constexpr int MIN_SPEED = 1;
	constexpr int DEFAULT_STEP = 1;
}

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
	auto *saveAction = new QAction("Save Ass");
	auto *openAction = new QAction("Open");
	auto *speedLabel = new QLabel("Speed:");

	auto *speedSpinBox = new QSpinBox;
	speedSpinBox->setRange(MIN_SPEED, MAX_SPEED);
	speedSpinBox->setValue(MIN_SPEED);
	speedSpinBox->setSingleStep(DEFAULT_STEP);

	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(openAction);

	QAction *labelAction = toolBar->addWidget(speedLabel);
	QAction *speedAction = toolBar->addWidget(speedSpinBox);

	toolBar->insertSeparator(saveAction);
	toolBar->insertSeparator(labelAction);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
	connect(openAction, &QAction::triggered, this, &GameWindow::open);
	connect(saveAction, &QAction::triggered, this, &GameWindow::saveAs);
	connect(speedSpinBox, &QSpinBox::valueChanged, &game_, &Game::changeSpeed);
}

void GameWindow::open()
{
	QString fileName = QFileDialog::getOpenFileName(this, QString("Open file"), QString(), QString("RLE (*.rle)"));
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
		QMessageBox::warning(this, "WARNING", QString("Can't open file %1:\n%2.").arg(QDir::toNativeSeparators(fileName), file.errorString()));
		return;
	}
	if (0 == file.size())
	{
		QMessageBox::warning(this, "WARNING", QString("An empty file"));
		return;
	}
	game_.setState(readState(&file));
}

void GameWindow::saveAs()
{
	QString fileName = QFileDialog::getSaveFileName(this, "Save file", "", "RLE (*.rle)");
	if (!fileName.isEmpty())
	{
		saveFile(fileName);
	}
}

void GameWindow::saveFile(const QString &fileName)
{
	QFile file(fileName);
	if (!file.open(QFile::WriteOnly | QFile::Text))
	{
		QMessageBox::warning(this, "WARNING", QString("Can't open file %1:\n%2.").arg(QDir::toNativeSeparators(fileName), file.errorString()));
		return;
	}
	saveToFile(&file, game_.getState());
}
