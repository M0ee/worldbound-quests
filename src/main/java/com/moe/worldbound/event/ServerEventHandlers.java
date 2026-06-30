package com.moe.worldbound.event;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.challenge.PunishmentDefinition;
import com.moe.worldbound.challenge.PunishmentWheel;
import com.moe.worldbound.network.WorldboundNetworking;
import com.moe.worldbound.quest.QuestDefinition;
import com.moe.worldbound.quest.QuestManager;
import com.moe.worldbound.quest.QuestTurnIn;
import com.moe.worldbound.state.WorldboundState;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;

import java.util.HashMap;
import java.util.Map;

public final class ServerEventHandlers {
	private static WorldboundState state;
	private static QuestManager quests;
	private static int tickCounter;

	private ServerEventHandlers() {
	}

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			state = WorldboundState.load(server);
			quests = new QuestManager(state);
			if (state.started) {
				com.moe.worldbound.border.BorderManager.apply(server, state);
			}
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (state != null) {
				state.save(server);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (state != null && quests != null) {
				WorldboundNetworking.sync(handler.getPlayer(), state, quests.activeQuest());
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (state == null || quests == null) {
				return;
			}
			tickCounter++;
			if (tickCounter % 100 == 0) {
				checkPlayers(server);
			}
			if (tickCounter % 100 == 0) {
				int day = (int) (server.overworld().getOverworldClockTime() / 24000L) + 1;
				quests.recordDay(server, day);
			}
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, entity, killed, damageSource) -> {
			if (state == null || quests == null || !(entity instanceof ServerPlayer player)) {
				return;
			}
			String killedId = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
			quests.recordEvent(level.getServer(), "kill_entity", killedId, 1);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (state == null || quests == null || alive || !WorldboundMod.config().deathPunishments) {
				return;
			}
			QuestDefinition activeQuest = quests.activeQuest();
			PunishmentDefinition punishment = PunishmentWheel.rollAndApply(newPlayer.level().getServer(), state, activeQuest, newPlayer);
			WorldboundMod.LOGGER.info("Rolled punishment {} for {}", punishment.id(), newPlayer.getScoreboardName());
			quests.saveAndSync(newPlayer.level().getServer());
		});

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
				return InteractionResult.PASS;
			}
			if (!entity.hasCustomName() || !"Worldbound Guide".equals(entity.getCustomName().getString())) {
				return InteractionResult.PASS;
			}
			QuestTurnIn.submit(serverLevel.getServer(), serverPlayer, state, quests, true);
			return InteractionResult.SUCCESS;
		});
	}

	public static WorldboundState state() {
		return state;
	}

	public static QuestManager quests() {
		return quests;
	}

	private static void checkPlayers(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			quests.recordInventory(server, inventoryCounts(player, quests.activeQuest().objective().targetId()));
			String dimension = player.level().dimension().identifier().toString();
			quests.recordEvent(server, "enter_dimension", dimension, 1);
			player.level().getBiome(player.blockPosition()).unwrapKey().ifPresent(key -> quests.recordEvent(server, "visit_biome", key.identifier().toString(), 1));
		}
	}

	private static Map<String, Integer> inventoryCounts(ServerPlayer player, String activeTargetId) {
		Map<String, Integer> counts = new HashMap<>();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (!stack.isEmpty()) {
				String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
				counts.merge(id, stack.getCount(), Integer::sum);
				if (activeTargetId.startsWith("#") && stack.is(tagKey(activeTargetId.substring(1)))) {
					counts.merge(activeTargetId, stack.getCount(), Integer::sum);
				}
			}
		}
		return counts;
	}

	private static TagKey<Item> tagKey(String id) {
		String[] parts = id.split(":", 2);
		String namespace = parts.length == 2 ? parts[0] : "minecraft";
		String path = parts.length == 2 ? parts[1] : parts[0];
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
