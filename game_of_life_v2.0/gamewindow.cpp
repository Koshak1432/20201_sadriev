#include "gamewindow.h"

#include <QLayout>
#include <QToolBar>
#include <QLabel>
#include <QScrollArea>
#include <QFileDialog>
#include <QMessageBox>
#include <QSpinBox>
#include <iostream>

#include "renderarea.h"
#include "io.h"
#include "rulesdialog.h"
#include "sizedialog.h"

namespace
{
	constexpr int MAX_SPEED = 100;
	constexpr int MIN_SPEED = 1;
	constexpr int DEFAULT_STEP = 1;
}

GameWindow::GameWindow()
						: game_(), rulesDialog_(new RulesDialog(game_.getState().getRules(),this)),
						sizeDialog_(new SizeDialog(QSize(game_.getState().getWidth(), game_.getState().getHeight()), this))
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
	auto *saveAction = new QAction("Save As");
	auto *openAction = new QAction("Open");
	auto *clearAction = new QAction("Clear");
	auto *speedLabel = new QLabel("Speed:");
	auto *rulesAction = new QAction("Rules");
	auto *sizeAction = new QAction("Change size");

	auto *speedSpinBox = new QSpinBox;
	speedSpinBox->setRange(MIN_SPEED, MAX_SPEED);
	speedSpinBox->setValue(MIN_SPEED);
	speedSpinBox->setSingleStep(DEFAULT_STEP);

	toolBar->addAction(playAction);
	toolBar->addAction(pauseAction);
	toolBar->addAction(saveAction);
	toolBar->addAction(openAction);
	toolBar->addAction(clearAction);

	QAction *labelAction = toolBar->addWidget(speedLabel);
	QAction *speedAction = toolBar->addWidget(speedSpinBox);
	toolBar->addAction(rulesAction);
	toolBar->addAction(sizeAction);

	toolBar->insertSeparator(saveAction);
	toolBar->insertSeparator(labelAction);

	connect(playAction, &QAction::triggered, &game_, &Game::play);
	connect(pauseAction, &QAction::triggered, &game_, &Game::pause);
	connect(openAction, &QAction::triggered, this, &GameWindow::open);
	connect(saveAction, &QAction::triggered, this, &GameWindow::saveAs);
	connect(speedSpinBox, &QSpinBox::valueChanged, &game_, &Game::changeSpeed);
	connect(clearAction, &QAction::triggered, &game_, &Game::clear);
	connect(rulesAction, &QAction::triggered, rulesDialog_, &QDialog::show);
	connect(sizeAction, &QAction::triggered, sizeDialog_, &QDialog::show);

	connect(rulesDialog_, &RulesDialog::birthBoxChanged, &game_, &Game::changeBirthRule);
	connect(rulesDialog_, &RulesDialog::survivalBoxChanged, &game_, &Game::changeSurvivalRule);
	connect(sizeDialog_, &SizeDialog::widthChanged, &game_, &Game::changeWidth);
	connect(sizeDialog_, &SizeDialog::heightChanged, &game_, &Game::changeHeight);
}

void GameWindow::open()
{
	QString fileName = QFileDialog::getOpenFileName(this, QString("Open file"),
													QString(), QString("RLE (*.rle)"));
	if (!fileName.isEmpty())
	{
		loadFile(fileName);
		return;
	}
}

void GameWindow::loadFile(const QString &fileName)
{
	QFile file(fileName);

	if (!file.open(QFile::ReadOnly | QFile::Text))
	{
		QMessageBox::warning(this, "WARNING",
							 QString("Can't open file %1:\n%2.").arg(QDir::toNativeSeparators(fileName),
																	 file.errorString()));
		return;
	}
	if (0 == file.size())
	{
		QMessageBox::warning(this, "WARNING", QString("An empty file"));
		return;
	}

	try
	{
		readState(&file, game_.getState());
	}
	catch(std::exception &e)
	{
		QMessageBox msg(QMessageBox::Warning, "ERROR", QString("Reading rle file error: %1\n%2\nField wasn't changed").arg(QDir::toNativeSeparators(fileName), e.what()));
		msg.exec();
		return;
	}
	rulesDialog_->changeBoxes(game_.getState().getRules());
	sizeDialog_->changeSizeSpinBoxes(QSize(game_.getState().getWidth(), game_.getState().getHeight()));
	game_.getScrollArea()->setWidget(new RenderArea(game_.getState().getCurrent()));
}

void GameWindow::saveAs()
{
	QString fileName = QFileDialog::getSaveFileName(this, "Save file", "", "RLE (*.rle)");
	if (!fileName.isEmpty())
	{
		saveFile(fileName);
		return;
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

