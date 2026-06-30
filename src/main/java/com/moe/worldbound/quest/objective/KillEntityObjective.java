package com.moe.worldbound.quest.objective;

import com.moe.worldbound.quest.QuestObjective;

public record KillEntityObjective(String targetId, int targetCount) implements QuestObjective {
	@Override
	public String type() {
		return "kill_entity";
	}

	@Override
	public String describe() {
		return "Defeat " + targetCount + " " + targetLabel();
	}
}
