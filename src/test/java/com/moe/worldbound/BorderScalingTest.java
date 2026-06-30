package com.moe.worldbound;

import com.moe.worldbound.border.BorderManager;
import com.moe.worldbound.quest.QuestChapter;
import com.moe.worldbound.state.WorldboundState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BorderScalingTest {
	@Test
	void shrinkNeverGoesBelowChapterMinimum() {
		WorldboundState state = new WorldboundState();
		state.borderDiameter = 200;
		BorderManager.shrink(state, 10_000, QuestChapter.SETTLEMENT);
		assertEquals(QuestChapter.SETTLEMENT.minimumBorder(), state.borderDiameter);
	}

	@Test
	void shrinkDoesNotExpandBorderWhenBelowChapterMinimum() {
		WorldboundState state = new WorldboundState();
		state.borderDiameter = 100;

		BorderManager.shrink(state, 64, QuestChapter.SETTLEMENT);

		assertEquals(100, state.borderDiameter);
	}

	@Test
	void expandAndShrinkIgnoreNegativeAmounts() {
		WorldboundState state = new WorldboundState();
		state.borderDiameter = 100;

		BorderManager.expand(state, -50);
		assertEquals(100, state.borderDiameter);

		BorderManager.shrink(state, -50, QuestChapter.SURVIVAL);
		assertEquals(100, state.borderDiameter);
	}
}
