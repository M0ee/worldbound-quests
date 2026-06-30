package com.moe.worldbound;

import com.moe.worldbound.network.QuestListEntry;
import com.moe.worldbound.quest.QuestRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestListEntryTest {
	@Test
	void questListEntrySnapshotsDisplayDataForClientScreen() {
		var quest = QuestRegistry.storyQuests().getFirst();
		var entry = QuestListEntry.from(quest);

		assertEquals("q01_arrival", entry.id());
		assertEquals("The Arrival", entry.title());
		assertEquals("SURVIVAL", entry.chapter());
		assertEquals("Reach day 1", entry.objective());
		assertEquals(1, entry.target());
		assertEquals(20, entry.reward());
		assertEquals(1, entry.unlockDay());
	}
}
