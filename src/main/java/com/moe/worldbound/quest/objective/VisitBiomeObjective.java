package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record VisitBiomeObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "visit_biome";
	}

	@Override
	public String describe() {
		return "Visit " + targetLabel();
	}
}
