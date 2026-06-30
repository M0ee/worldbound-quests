package com.moe.worldbound;

import com.moe.worldbound.quest.QuestRegistry;
import com.moe.worldbound.quest.objective.ObtainItemObjective;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestManagerTest {
	@Test
	void storyCampaignHasDragonQuest() {
		assertTrue(QuestRegistry.indexOf("q50_dragonfall").isPresent());
		assertEquals("q01_arrival", QuestRegistry.storyQuests().getFirst().id());
	}

	@Test
	void storyCampaignLoadsFullResourceCampaign() {
		assertEquals(50, QuestRegistry.storyQuests().size());
		assertEquals("Punching Wood", QuestRegistry.storyQuests().get(1).title());
		assertEquals(5000, QuestRegistry.storyQuests().getLast().reward().borderExpansion());
	}

	@Test
	void storyCampaignReachesDragonScaleBorderBeforeFinalFight() {
		int expansionBeforeDragon = QuestRegistry.storyQuests().stream()
				.filter(quest -> !quest.id().equals("dragonfall"))
				.mapToInt(quest -> quest.reward().borderExpansion())
				.sum();

		assertTrue(expansionBeforeDragon >= 6000);
		assertTrue(expansionBeforeDragon <= 20_000);
	}

	@Test
	void storyCampaignParsesObjectiveTypesFromJson() {
		var firstQuest = QuestRegistry.storyQuests().getFirst();

		assertEquals("reach_day", firstQuest.objective().type());
		assertEquals("minecraft:overworld", firstQuest.objective().targetId());
		assertEquals(1, firstQuest.objective().targetCount());
	}

	@Test
	void storyCampaignIsTunedForTwoPlayerChallenge() {
		assertEquals(32, QuestRegistry.storyQuests().get(3).objective().targetCount());
		assertEquals(16, QuestRegistry.storyQuests().get(10).objective().targetCount());
		assertEquals(10, QuestRegistry.storyQuests().get(27).objective().targetCount());
	}

	@Test
	void midCampaignQuestIsNotBasicStringCollection() {
		var quest = QuestRegistry.storyQuests().get(27);

		assertEquals("Rod of Power", quest.title());
		assertEquals("obtain_item", quest.objective().type());
		assertEquals("minecraft:blaze_rod", quest.objective().targetId());
		assertEquals(10, quest.objective().targetCount());
	}

	@Test
	void storyCampaignUsesDayGatesUpToDragon() {
		assertEquals(1, QuestRegistry.storyQuests().getFirst().unlockDay());
		assertEquals(25, QuestRegistry.storyQuests().get(24).unlockDay());
		assertEquals(50, QuestRegistry.storyQuests().getLast().unlockDay());
		assertEquals("Dragonfall", QuestRegistry.storyQuests().getLast().title());
	}

	@Test
	void questsReportWhetherCurrentDayUnlocksThem() {
		var dragon = QuestRegistry.storyQuests().getLast();

		assertTrue(!dragon.isUnlocked(49));
		assertTrue(dragon.isUnlocked(50));
	}

	@Test
	void earlyMissionsGiveVisibleRecordingProgress() {
		int firstFiveRewards = QuestRegistry.storyQuests().stream()
				.limit(5)
				.mapToInt(quest -> quest.reward().borderExpansion())
				.sum();

		assertTrue(firstFiveRewards >= 120);
	}

	@Test
	void defaultCampaignAvoidsBiomeRngGates() {
		assertTrue(QuestRegistry.storyQuests().stream().noneMatch(quest -> quest.objective().type().equals("visit_biome")));
	}

	@Test
	void rareDropRequirementsStayRecordingFriendly() {
		var ghastTearQuest = QuestRegistry.storyQuests().stream()
				.filter(quest -> quest.objective().targetId().equals("minecraft:ghast_tear"))
				.findFirst()
				.orElseThrow();

		assertTrue(ghastTearQuest.objective().targetCount() <= 1);
	}
}
