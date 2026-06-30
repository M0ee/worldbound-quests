package com.moe.worldbound;

import com.moe.worldbound.config.WorldboundConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorldboundConfigTest {
	@Test
	void sanitizeKeepsConfigInPlayableRange() {
		WorldboundConfig config = new WorldboundConfig();
		config.startingBorderDiameter = 1;
		config.expansionMultiplier = 0;
		config.punishmentSeverity = -1;
		config.campaignDayTarget = 1;

		config.sanitize();

		assertEquals(16, config.startingBorderDiameter);
		assertEquals(0.1, config.expansionMultiplier);
		assertEquals(0.0, config.punishmentSeverity);
		assertEquals(20, config.campaignDayTarget);
	}
}
