package com.moe.worldbound.challenge;

public record PunishmentDefinition(
		String id,
		String title,
		String type,
		int weight,
		double amount,
		int durationSeconds
) {
}
