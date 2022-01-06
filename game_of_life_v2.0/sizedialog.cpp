#include "sizedialog.h"

#include <QVBoxLayout>
#include <QLabel>
#include <QSpinBox>
#include <QPushButton>

namespace
{
	constexpr int MAX_SIZE = 120;
	constexpr int MIN_SIZE = 10;
	constexpr int DEFAULT_STEP = 1;
}


SizeDialog::SizeDialog(const QSize &size, QWidget *parent)
							: QDialog(parent), widthSpinBox(createSpinBox(size.width(), WIDTH)),
							  heightSpinBox(createSpinBox(size.height(), HEIGHT))
{
	setWindowTitle("Size options");
	auto *mainLayout = new QVBoxLayout;
	auto *minSizeLabel = new QLabel(QString("Min size: %1").arg(MIN_SIZE));
	auto *maxSizeLabel = new QLabel(QString("Max size: %1").arg(MAX_SIZE));

	auto *widthLabel = new QLabel("Width:");
	auto *heightLabel = new QLabel("Height:");
	auto *widthLayout = new QHBoxLayout;
	auto *heightLayout = new QHBoxLayout;
	auto *okButton = new QPushButton("OK", this);
	okButton->setDefault(true);

	widthLayout->addWidget(widthLabel);
	widthLayout->addWidget(widthSpinBox);
	heightLayout->addWidget(heightLabel);
	heightLayout->addWidget(heightSpinBox);
	mainLayout->addWidget(minSizeLabel);
	mainLayout->addWidget(maxSizeLabel);
	mainLayout->addLayout(widthLayout);
	mainLayout->addLayout(heightLayout);
	mainLayout->addWidget(okButton);

	connect(okButton, &QPushButton::clicked, this, &QDialog::accept);

	this->setLayout(mainLayout);
	this->setModal(true);
}

QSpinBox *SizeDialog::createSpinBox(int value, SizeType type)
{
	auto *spinBox = new QSpinBox;
	spinBox->setSingleStep(DEFAULT_STEP);
	spinBox->setValue(value);
	spinBox->setRange(MIN_SIZE, MAX_SIZE);
	connect(spinBox, &QSpinBox::valueChanged, [this, spinBox, type]()
	{
		(type == WIDTH) ? emit widthChanged(spinBox->value()) : emit heightChanged(spinBox->value());
	});
	return spinBox;
}

void SizeDialog::changeSizeSpinBoxes(QSize size)
{
	widthSpinBox->setValue(size.width());
	heightSpinBox->setValue(size.height());
}
