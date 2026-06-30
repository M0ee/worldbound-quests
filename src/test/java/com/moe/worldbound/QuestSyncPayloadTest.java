package com.moe.worldbound;

import com.moe.worldbound.network.QuestListEntry;
import com.moe.worldbound.network.QuestSyncPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestSyncPayloadTest {
	@Test
	void payloadCarriesDayAndExpandedBorderForClientUi() {
		QuestSyncPayload payload = new QuestSyncPayload(
				"basic_table",
				"Build a Workbench",
				"Craft 1 crafting table",
				"0/1 - Craft 1 crafting table",
				0,
				1,
				72,
				40,
				3,
				true,
				false,
				true,
				1,
				List.of(new QuestListEntry("basic_table", "Build a Workbench", "SURVIVAL", 1, "Craft 1 crafting table", 1, 16))
		);

		assertEquals(40, payload.expandedBlocks());
		assertEquals(3, payload.campaignDay());
	}
}
