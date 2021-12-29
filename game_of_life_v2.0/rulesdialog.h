#ifndef GAME_OF_LIFE_V2_0_RULESDIALOG_H
#define GAME_OF_LIFE_V2_0_RULESDIALOG_H

#include <QDialog>
#include <QGroupBox>
#include <QCheckBox>

#include "engine.h"

class RulesDialog : public QDialog
{
	Q_OBJECT

public:
	explicit RulesDialog(const Rules &rules, QWidget *parent = nullptr);
	void changeBoxes(const Rules &rules);

signals:
	void birthBoxChanged(int idx, bool checked);
	void survivalBoxChanged(int idx, bool checked);

private:
	QList<QCheckBox *> birthRules_;
	QList<QCheckBox *> survivalRules_;

	enum RulesType
	{
		BIRTH,
		SURVIVAL
	};
	QList<QCheckBox *> initRulesList(RulesType type, QLayout *layout);
};

#endif //GAME_OF_LIFE_V2_0_RULESDIALOG_H
