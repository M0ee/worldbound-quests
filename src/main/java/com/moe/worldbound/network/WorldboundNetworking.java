package com.moe.worldbound.network;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.quest.QuestDefinition;
import com.moe.worldbound.quest.QuestRegistry;
import com.moe.worldbound.state.WorldboundState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class WorldboundNetworking {
	private WorldboundNetworking() {
	}

	public static void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(QuestSyncPayload.TYPE, QuestSyncPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PunishmentSyncPayload.TYPE, PunishmentSyncPayload.CODEC);
	}

	public static void syncAll(MinecraftServer server, WorldboundState state, QuestDefinition activeQuest) {
		QuestSyncPayload payload = snapshot(server, state, activeQuest);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerPlayNetworking.send(player, payload);
			if (!state.lastPunishment.isBlank()) {
				ServerPlayNetworking.send(player, new PunishmentSyncPayload(state.lastPunishment));
			}
		}
	}

	public static void sync(ServerPlayer player, WorldboundState state, QuestDefinition activeQuest) {
		ServerPlayNetworking.send(player, snapshot(player.level().getServer(), state, activeQuest));
		if (!state.lastPunishment.isBlank()) {
			ServerPlayNetworking.send(player, new PunishmentSyncPayload(state.lastPunishment));
		}
	}

	private static QuestSyncPayload snapshot(MinecraftServer server, WorldboundState state, QuestDefinition activeQuest) {
		int currentDay = currentDay(server);
		state.campaignDay = Math.max(state.campaignDay, currentDay);
		int expandedBlocks = Math.max(0, (int) Math.round(state.borderDiameter - WorldboundMod.config().startingBorderDiameter));
		return new QuestSyncPayload(
				activeQuest.id(),
				activeQuest.title(),
				activeQuest.objective().describe(),
				activeQuest.progressText(state.activeQuestProgress),
				state.activeQuestProgress,
				activeQuest.objective().targetCount(),
				state.borderDiameter,
				expandedBlocks,
				currentDay,
				state.started,
				state.paused,
				WorldboundMod.config().hudVisible,
				state.activeQuestIndex,
				questList()
		);
	}

	private static List<QuestListEntry> questList() {
		return QuestRegistry.storyQuests().stream()
				.map(QuestListEntry::from)
				.toList();
	}

	private static int currentDay(MinecraftServer server) {
		return (int) (server.overworld().getOverworldClockTime() / 24000L) + 1;
	}
}
