package com.moe.worldbound.state;

import java.util.UUID;

public final class PlayerRunState {
	private final UUID playerId;
	private int deaths;
	private long lastPunishmentGameTime;

	public PlayerRunState(UUID playerId) {
		this.playerId = playerId;
	}

	public UUID playerId() {
		return playerId;
	}

	public int deaths() {
		return deaths;
	}

	public long lastPunishmentGameTime() {
		return lastPunishmentGameTime;
	}

	public void recordDeath(long gameTime) {
		deaths++;
		lastPunishmentGameTime = gameTime;
	}
}
