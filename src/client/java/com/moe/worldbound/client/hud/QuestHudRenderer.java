package com.moe.worldbound.client.hud;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.network.QuestSyncPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class QuestHudRenderer {
	private static QuestSyncPayload quest;
	private static String punishment = "";

	private QuestHudRenderer() {
	}

	public static void register() {
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(WorldboundMod.MOD_ID, "quest_hud"),
				QuestHudRenderer::render
		);
	}

	public static void updateQuest(QuestSyncPayload payload) {
		quest = payload;
	}

	public static QuestSyncPayload currentQuest() {
		return quest;
	}

	public static void updatePunishment(String message) {
		punishment = message == null ? "" : message;
	}

	private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		QuestSyncPayload snapshot = quest;
		if (snapshot == null || !snapshot.hudVisible() || !snapshot.started()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		int x = 6;
		int y = 6;
		int width = 226;
		int height = 65;
		int innerWidth = width - 14;
		int progressWidth = snapshot.target() <= 0 ? 0 : (int) Math.round(innerWidth * Math.min(1.0, snapshot.progress() / (double) snapshot.target()));

		graphics.fill(x + 1, y + 1, x + width + 1, y + height + 1, 0xAA050608);
		graphics.fillGradient(x, y, x + width, y + height, 0xE515101A, 0xD5081D22);
		graphics.outline(x, y, width, height, snapshot.paused() ? 0xE0C88335 : 0xE0D5B35A);
		graphics.outline(x + 2, y + 2, width - 4, height - 4, 0x8038F2D2);
		graphics.fill(x + 7, y + 7, x + width - 7, y + 8, 0x99E0BC66);
		graphics.text(minecraft.font, Component.literal(fit(minecraft, snapshot.paused() ? "Worldbound Paused" : snapshot.title(), innerWidth)), x + 7, y + 11, 0xFFFFF1C4);
		graphics.text(minecraft.font, Component.literal(fit(minecraft, snapshot.objective(), innerWidth)), x + 7, y + 22, 0xFFE8E2D2);
		graphics.text(minecraft.font, Component.literal(fit(minecraft, snapshot.progressText(), innerWidth - 58)), x + 7, y + 33, 0xFF9FEBDD);
		graphics.text(minecraft.font, Component.literal("K Quests"), x + width - 54, y + 33, 0xFFE0BC66);
		graphics.text(minecraft.font, Component.literal("Day " + snapshot.campaignDay() + " | Border " + Math.round(snapshot.borderDiameter()) + " | +" + snapshot.expandedBlocks()), x + 7, y + 44, 0xFFCFC6A8);
		graphics.fill(x + 7, y + 55, x + width - 7, y + 60, 0xFF181512);
		graphics.fill(x + 7, y + 55, x + 7 + progressWidth, y + 60, 0xFFE0BC66);
		graphics.fill(x + 7, y + 59, x + 7 + progressWidth, y + 60, 0xFF55E7CC);

		if (!punishment.isBlank()) {
			String text = "Punishment: " + punishment;
			String firstLine = wrapLine(minecraft, text, 210);
			String secondLine = secondLine(minecraft, text, firstLine, 210);
			int punishmentY = y + height + 4;
			int punishmentHeight = secondLine.isBlank() ? 18 : 29;
			graphics.fill(x, punishmentY, x + 220, punishmentY + punishmentHeight, 0x88000000);
			graphics.outline(x, punishmentY, 220, punishmentHeight, 0xCCFF7777);
			graphics.text(minecraft.font, Component.literal(firstLine), x + 5, punishmentY + 5, 0xFFFFCCCC);
			if (!secondLine.isBlank()) {
				graphics.text(minecraft.font, Component.literal(secondLine), x + 5, punishmentY + 16, 0xFFFFCCCC);
			}
		}
	}

	private static String fit(Minecraft minecraft, String text, int maxWidth) {
		if (minecraft.font.width(text) <= maxWidth) {
			return text;
		}
		String suffix = "...";
		int suffixWidth = minecraft.font.width(suffix);
		StringBuilder builder = new StringBuilder(text);
		while (!builder.isEmpty() && minecraft.font.width(builder.toString()) + suffixWidth > maxWidth) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder + suffix;
	}

	private static String wrapLine(Minecraft minecraft, String text, int maxWidth) {
		if (minecraft.font.width(text) <= maxWidth) {
			return text;
		}
		int lastSpace = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				lastSpace = i;
			}
			if (minecraft.font.width(text.substring(0, i + 1)) > maxWidth) {
				return text.substring(0, Math.max(1, lastSpace)).trim();
			}
		}
		return text;
	}

	private static String secondLine(Minecraft minecraft, String original, String firstLine, int maxWidth) {
		if (firstLine.length() >= original.length()) {
			return "";
		}
		String rest = original.substring(firstLine.length()).trim();
		return rest.isBlank() ? "" : fit(minecraft, rest, maxWidth);
	}
}
