package com.moe.worldbound.network;

import com.moe.worldbound.quest.QuestDefinition;

public record QuestListEntry(
		String id,
		String title,
		String chapter,
		int unlockDay,
		String objective,
		int target,
		int reward
) {
	public static QuestListEntry from(QuestDefinition quest) {
		return new QuestListEntry(
				quest.id(),
				quest.title(),
				quest.chapter().name(),
				quest.unlockDay(),
				quest.objective().describe(),
				quest.objective().targetCount(),
				quest.reward().borderExpansion()
		);
	}
}
