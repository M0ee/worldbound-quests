package com.moe.worldbound.client;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.client.hud.QuestHudRenderer;
import com.moe.worldbound.client.screen.QuestBookScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class WorldboundQuestControls {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(WorldboundMod.MOD_ID, "controls"));
	private static KeyMapping openQuestBook;

	private WorldboundQuestControls() {
	}

	public static void register() {
		openQuestBook = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.worldbound.open_quests",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openQuestBook.consumeClick()) {
				if (client.screen == null && QuestHudRenderer.currentQuest() != null) {
					client.setScreen(new QuestBookScreen(QuestHudRenderer.currentQuest()));
				}
			}
		});
	}
}
