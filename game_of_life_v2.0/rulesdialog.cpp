#include "rulesdialog.h"

#include <QLayout>
#include <QCheckBox>
#include <QLabel>
#include <QPushButton>

RulesDialog::RulesDialog(const Rules &rules, QWidget *parent) : QDialog(parent)
{
	setWindowTitle("Choosing rules");
	auto *mainLayout = new QVBoxLayout;

	auto *birthLabel = new QLabel("Birth rules:");
	auto *survivalLabel = new QLabel("Survival rules:");
	auto *birthLayout = new QHBoxLayout;
	auto *survivalLayout = new QHBoxLayout;
	birthRules_ = initRulesList(BIRTH, birthLayout);
	survivalRules_ = initRulesList(SURVIVAL, survivalLayout);

	auto *okButton = new QPushButton("OK", this);
	okButton->setDefault(true);

	mainLayout->addWidget(birthLabel);
	mainLayout->addLayout(birthLayout);
	mainLayout->addWidget(survivalLabel);
	mainLayout->addLayout(survivalLayout);
	mainLayout->addWidget(okButton);
	connect(okButton, &QPushButton::clicked, this, &QDialog::accept);

	changeBoxes(rules);
	this->setLayout(mainLayout);
	this->setModal(true);
}

QList<QCheckBox *> RulesDialog::initRulesList(RulesType type, QLayout *layout)
{
	QList<QCheckBox *> rules;
	for (int i = 0; i < 9; ++i)
	{
		auto *box = new QCheckBox(QString("%1").arg(i));
		connect(box, &QCheckBox::stateChanged, [this, type, i] (int state)
		{
			(BIRTH == type) ? emit birthBoxChanged(i, state == Qt::Checked) : emit survivalBoxChanged(i, state == Qt::Checked);
		});
		layout->addWidget(box);
		rules.push_back(box);
	}
	return rules;
}

void RulesDialog::changeBoxes(const Rules &rules)
{
	for (int i = 0; i < birthRules_.size(); ++i)
	{
		rules.birth_[i] ? birthRules_[i]->setCheckState(Qt::Checked) : birthRules_[i]->setCheckState(Qt::Unchecked);
		rules.survival_[i] ? survivalRules_[i]->setCheckState(Qt::Checked) : survivalRules_[i]->setCheckState(Qt::Unchecked);
	}
}

