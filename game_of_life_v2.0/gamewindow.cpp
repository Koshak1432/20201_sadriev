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

constexpr int MAX_SPEED = 100;
constexpr int MIN_SPEED = 1;

constexpr int DEFAULT_STEP = 1;

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
	auto *speedLabel = new QLabel("Speed:");
	auto *speedSpinBox = new QSpinBox;
	speedSpinBox->setRange(MIN_SPEED, MAX_SPEED);
	speedSpinBox->setValue(MIN_SPEED);
	speedSpinBox->setSingleStep(DEFAULT_STEP);

	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(openAction);
	toolBar->addWidget(speedLabel);
	toolBar->addWidget(speedSpinBox);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
	connect(openAction, &QAction::triggered, this, &GameWindow::open);
	connect(speedSpinBox, &QSpinBox::valueChanged, &game_, &Game::changeSpeed);
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
	game_.setState(readState(&file));
}
