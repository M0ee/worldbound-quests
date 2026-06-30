package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record CraftItemObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "craft_item";
	}

	@Override
	public String describe() {
		return "Craft " + targetCount + " " + targetLabel();
	}
}
