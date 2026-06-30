package com.moe.worldbound.quest;

public final class QuestProgress {
	private int amount;

	public QuestProgress(int amount) {
		this.amount = Math.max(0, amount);
	}

	public int amount() {
		return amount;
	}

	public boolean setAmount(int amount) {
		int sanitized = Math.max(0, amount);
		if (this.amount == sanitized) {
			return false;
		}
		this.amount = sanitized;
		return true;
	}

	public boolean add(int delta) {
		return setAmount(amount + delta);
	}
}
