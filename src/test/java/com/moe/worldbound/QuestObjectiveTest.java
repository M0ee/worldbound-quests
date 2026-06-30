package com.moe.worldbound;

import com.moe.worldbound.quest.objective.KillEntityObjective;
import com.moe.worldbound.quest.objective.ObtainItemObjective;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestObjectiveTest {
	@Test
	void itemObjectiveReadsProgressFromInventoryCounts() {
		ObtainItemObjective objective = new ObtainItemObjective("minecraft:iron_ingot", 24);

		assertEquals(13, objective.progressFromInventory(Map.of("minecraft:iron_ingot", 13)));
		assertEquals(0, objective.progressFromInventory(Map.of("minecraft:copper_ingot", 64)));
	}

	@Test
	void eventObjectiveRequiresMatchingTypeAndTarget() {
		KillEntityObjective objective = new KillEntityObjective("minecraft:zombie", 3);

		assertTrue(objective.matchesEvent("kill_entity", "minecraft:zombie"));
		assertFalse(objective.matchesEvent("kill_entity", "minecraft:skeleton"));
		assertFalse(objective.matchesEvent("obtain_item", "minecraft:zombie"));
	}
}
