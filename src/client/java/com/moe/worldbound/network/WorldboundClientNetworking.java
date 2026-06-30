package com.moe.worldbound.network;

import com.moe.worldbound.client.hud.QuestHudRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class WorldboundClientNetworking {
	private WorldboundClientNetworking() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(QuestSyncPayload.TYPE, (payload, context) -> QuestHudRenderer.updateQuest(payload));
		ClientPlayNetworking.registerGlobalReceiver(PunishmentSyncPayload.TYPE, (payload, context) -> QuestHudRenderer.updatePunishment(payload.message()));
	}
}
