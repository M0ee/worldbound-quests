package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record ObtainItemObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "obtain_item";
	}

	@Override
	public String describe() {
		return "Collect " + targetCount + " " + targetLabel();
	}
}
