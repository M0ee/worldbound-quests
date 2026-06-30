package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record EnterDimensionObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "enter_dimension";
	}

	@Override
	public String describe() {
		return "Enter " + targetLabel();
	}
}
