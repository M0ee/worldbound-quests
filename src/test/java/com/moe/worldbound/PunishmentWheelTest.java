package com.moe.worldbound;

import com.moe.worldbound.challenge.PunishmentWheel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PunishmentWheelTest {
	@Test
	void rollReturnsWeightedPunishment() {
		var punishment = PunishmentWheel.roll();
		assertNotNull(punishment.id());
		assertTrue(punishment.weight() > 0);
	}

	@Test
	void resourcePunishmentsAreRealisticSurvivalSet() {
		var punishments = PunishmentWheel.definitions();

		assertTrue(punishments.size() >= 9);
		assertTrue(punishments.stream().anyMatch(punishment -> punishment.type().equals("effect_hunger")));
		assertTrue(punishments.stream().anyMatch(punishment -> punishment.type().equals("effect_weakness")));
		assertTrue(punishments.stream().anyMatch(punishment -> punishment.type().equals("effect_darkness")));
		assertTrue(punishments.stream().anyMatch(punishment -> punishment.type().equals("food_drain")));
		assertFalse(punishments.stream().anyMatch(punishment -> punishment.type().equals("xp_drain")));
		assertFalse(punishments.stream().anyMatch(punishment -> punishment.type().equals("side_task")));
		assertFalse(punishments.stream().anyMatch(punishment -> punishment.type().equals("delete_item")));
		assertTrue(punishments.stream().allMatch(punishment -> punishment.weight() > 0));
	}

	@Test
	void borderPunishmentsStaySmallEnoughForRecording() {
		var largestBorderPunishment = PunishmentWheel.definitions().stream()
				.filter(punishment -> punishment.type().equals("border_shrink"))
				.mapToDouble(punishment -> punishment.amount())
				.max()
				.orElse(0);

		assertTrue(largestBorderPunishment <= 24);
	}
}
