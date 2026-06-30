package com.moe.worldbound;

import com.moe.worldbound.client.hud.QuestHudRenderer;
import com.moe.worldbound.client.WorldboundQuestControls;
import com.moe.worldbound.network.WorldboundClientNetworking;
import net.fabricmc.api.ClientModInitializer;

public final class WorldboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WorldboundClientNetworking.register();
		WorldboundQuestControls.register();
		QuestHudRenderer.register();
	}
}
