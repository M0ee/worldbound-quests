package com.moe.worldbound.quest;

public enum QuestChapter {
	SURVIVAL(1, 10, 32),
	SETTLEMENT(11, 30, 128),
	NETHER_PREP(31, 55, 512),
	BLAZE_AND_PEARLS(56, 75, 1500),
	DRAGON(76, 100, 6000);

	private final int startDay;
	private final int endDay;
	private final int minimumBorder;

	QuestChapter(int startDay, int endDay, int minimumBorder) {
		this.startDay = startDay;
		this.endDay = endDay;
		this.minimumBorder = minimumBorder;
	}

	public int startDay() {
		return startDay;
	}

	public int endDay() {
		return endDay;
	}

	public int minimumBorder() {
		return minimumBorder;
	}
}
