package com.moe.worldbound.challenge;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.border.BorderManager;
import com.moe.worldbound.event.WorldboundFeedback;
import com.moe.worldbound.quest.QuestDefinition;
import com.moe.worldbound.state.WorldboundState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class PunishmentWheel {
	private static final String PUNISHMENT_RESOURCE = "/data/worldbound/punishments/punishment_wheel.json";
	private static final Random RANDOM = new Random();
	private static final List<PunishmentDefinition> DEFAULTS = Collections.unmodifiableList(List.of(
			new PunishmentDefinition("border_shrink", "The border contracts", "border_shrink", 5, 64, 0),
			new PunishmentDefinition("slowness", "Heavy steps", "effect_slowness", 4, 1, 180),
			new PunishmentDefinition("mining_fatigue", "Dull tools", "effect_mining_fatigue", 3, 1, 180),
			new PunishmentDefinition("hunger", "Empty stomachs", "effect_hunger", 3, 1, 120),
			new PunishmentDefinition("weakness", "Shaken arms", "effect_weakness", 3, 1, 120),
			new PunishmentDefinition("darkness", "The world goes quiet", "effect_darkness", 2, 1, 35),
			new PunishmentDefinition("food_drain", "Rations spoiled", "food_drain", 2, 6, 0),
			new PunishmentDefinition("reward_tax", "Next quest reward reduced", "reward_tax", 4, 0.75, 0)
	));
	private static final List<PunishmentDefinition> DEFINITIONS = loadDefinitions();

	private PunishmentWheel() {
	}

	public static List<PunishmentDefinition> definitions() {
		return DEFINITIONS;
	}

	public static PunishmentDefinition roll() {
		int total = DEFINITIONS.stream().mapToInt(PunishmentDefinition::weight).sum();
		int pick = RANDOM.nextInt(Math.max(1, total));
		for (PunishmentDefinition punishment : DEFINITIONS) {
			pick -= punishment.weight();
			if (pick < 0) {
				return punishment;
			}
		}
		return DEFINITIONS.getFirst();
	}

	public static PunishmentDefinition rollAndApply(MinecraftServer server, WorldboundState state, QuestDefinition activeQuest, ServerPlayer player) {
		PunishmentDefinition punishment = roll();
		apply(server, state, activeQuest, player, punishment);
		return punishment;
	}

	public static void apply(MinecraftServer server, WorldboundState state, QuestDefinition activeQuest, ServerPlayer player, PunishmentDefinition punishment) {
		double severity = WorldboundMod.config().punishmentSeverity;
		switch (punishment.type()) {
			case "border_shrink" -> {
				BorderManager.shrink(state, punishment.amount() * severity, activeQuest.chapter());
				BorderManager.apply(server, state);
			}
			case "effect_slowness" -> player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks(punishment, severity), 0));
			case "effect_mining_fatigue" -> player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, ticks(punishment, severity), 0));
			case "effect_hunger" -> player.addEffect(new MobEffectInstance(MobEffects.HUNGER, ticks(punishment, severity), 0));
			case "effect_weakness" -> player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks(punishment, severity), 0));
			case "effect_blindness" -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ticks(punishment, severity), 0));
			case "effect_darkness" -> player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, ticks(punishment, severity), 0));
			case "food_drain" -> drainFood(player, punishment.amount(), severity);
			case "reward_tax" -> state.nextRewardMultiplier = Math.min(state.nextRewardMultiplier, Math.max(0.25, punishment.amount()));
			default -> {
			}
		}
		state.lastPunishment = punishment.title();
		server.getPlayerList().broadcastSystemMessage(Component.literal("[Worldbound] " + player.getScoreboardName() + " rolled punishment: " + punishment.title()), false);
		WorldboundFeedback.punishment(server, state.lastPunishment);
	}

	private static int ticks(PunishmentDefinition punishment, double severity) {
		return Math.max(20, (int) Math.round(punishment.durationSeconds() * 20.0 * Math.max(0.1, severity)));
	}

	private static void drainFood(ServerPlayer player, double amount, double severity) {
		int drain = Math.max(0, (int) Math.round(amount * Math.max(0.0, severity)));
		int newFoodLevel = Math.max(6, player.getFoodData().getFoodLevel() - drain);
		player.getFoodData().setFoodLevel(newFoodLevel);
		player.getFoodData().setSaturation(0.0F);
	}

	private static List<PunishmentDefinition> loadDefinitions() {
		try (InputStream stream = PunishmentWheel.class.getResourceAsStream(PUNISHMENT_RESOURCE)) {
			if (stream == null) {
				return DEFAULTS;
			}
			try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				List<PunishmentDefinition> punishments = parsePunishments(JsonParser.parseReader(reader).getAsJsonObject());
				return punishments.isEmpty() ? DEFAULTS : Collections.unmodifiableList(punishments);
			}
		} catch (IOException | IllegalStateException exception) {
			return DEFAULTS;
		}
	}

	private static List<PunishmentDefinition> parsePunishments(JsonObject root) {
		JsonArray entries = root.getAsJsonArray("punishments");
		List<PunishmentDefinition> punishments = new ArrayList<>();
		if (entries == null) {
			return punishments;
		}
		for (int i = 0; i < entries.size(); i++) {
			JsonObject entry = entries.get(i).getAsJsonObject();
			punishments.add(new PunishmentDefinition(
					entry.get("id").getAsString(),
					entry.get("title").getAsString(),
					entry.get("type").getAsString(),
					Math.max(1, entry.get("weight").getAsInt()),
					entry.has("amount") ? entry.get("amount").getAsDouble() : 1.0,
					entry.has("durationSeconds") ? entry.get("durationSeconds").getAsInt() : 0
			));
		}
		return punishments;
	}
}
