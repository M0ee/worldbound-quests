package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record ReachDayObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "reach_day";
	}

	@Override
	public String describe() {
		return "Reach day " + targetCount;
	}
}
