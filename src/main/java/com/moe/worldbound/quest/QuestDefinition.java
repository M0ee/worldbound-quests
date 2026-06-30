package com.moe.worldbound.quest;

public record QuestDefinition(
		String id,
		String title,
		int unlockDay,
		QuestChapter chapter,
		QuestObjective objective,
		QuestReward reward
) {
	public QuestDefinition(String id, String title, QuestChapter chapter, QuestObjective objective, QuestReward reward) {
		this(id, title, chapter.startDay(), chapter, objective, reward);
	}

	public String progressText(int progress) {
		int capped = Math.min(progress, objective.targetCount());
		return capped + "/" + objective.targetCount() + " - " + objective.describe();
	}

	public boolean isUnlocked(int campaignDay) {
		return Math.max(1, campaignDay) >= unlockDay;
	}
}
