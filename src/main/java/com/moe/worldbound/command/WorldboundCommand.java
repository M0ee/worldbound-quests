package com.moe.worldbound.command;

import com.moe.worldbound.border.BorderManager;
import com.moe.worldbound.challenge.PunishmentWheel;
import com.moe.worldbound.event.GuideVillagerSpawner;
import com.moe.worldbound.event.ServerEventHandlers;
import com.moe.worldbound.quest.QuestManager;
import com.moe.worldbound.quest.QuestRegistry;
import com.moe.worldbound.quest.QuestTurnIn;
import com.moe.worldbound.state.WorldboundState;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.phys.Vec3;

public final class WorldboundCommand {
	private WorldboundCommand() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				Commands.literal("worldbound")
						.then(Commands.literal("status").executes(context -> status(context.getSource())))
						.then(Commands.literal("start").executes(context -> start(context.getSource())))
						.then(Commands.literal("submit").executes(context -> submit(context.getSource())))
						.then(Commands.literal("pause").requires(WorldboundCommand::isAdmin).executes(context -> pause(context.getSource())))
						.then(Commands.literal("nextquest").requires(WorldboundCommand::isAdmin).executes(context -> nextQuest(context.getSource())))
						.then(Commands.literal("setquest")
								.requires(WorldboundCommand::isAdmin)
								.then(Commands.argument("id", StringArgumentType.word())
										.suggests((context, builder) -> {
											QuestRegistry.storyQuests().forEach(quest -> builder.suggest(quest.id()));
											return builder.buildFuture();
										})
										.executes(context -> setQuest(context.getSource(), StringArgumentType.getString(context, "id")))))
						.then(Commands.literal("expand")
								.requires(WorldboundCommand::isAdmin)
								.then(Commands.argument("blocks", IntegerArgumentType.integer(1))
										.executes(context -> expand(context.getSource(), IntegerArgumentType.getInteger(context, "blocks")))))
						.then(Commands.literal("rollpunishment").requires(WorldboundCommand::isAdmin).executes(context -> rollPunishment(context.getSource())))
						.then(Commands.literal("spawnguide").requires(WorldboundCommand::isAdmin).executes(context -> spawnGuide(context.getSource())))
						.then(Commands.literal("resetrun").requires(WorldboundCommand::isAdmin).executes(context -> resetRun(context.getSource())))
		));
	}

	private static boolean isAdmin(CommandSourceStack source) {
		return source.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
	}

	private static int status(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		QuestManager quests = ServerEventHandlers.quests();
		if (state == null || quests == null) {
			return message(source, "Worldbound is not ready yet.");
		}
		return message(source, "Quest: " + quests.activeQuest().title() + " | " + quests.activeQuest().progressText(state.activeQuestProgress) + " | border " + Math.round(state.borderDiameter));
	}

	private static int start(CommandSourceStack source) {
		QuestManager quests = ServerEventHandlers.quests();
		if (quests == null) {
			return message(source, "Worldbound is not ready yet.");
		}
		Vec3 position = source.getPosition();
		quests.start(source.getServer(), position.x(), position.z());
		GuideVillagerSpawner.spawn(source.getServer().overworld(), ServerEventHandlers.state());
		return message(source, "Worldbound run started.");
	}

	private static int pause(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		QuestManager quests = ServerEventHandlers.quests();
		if (state == null || quests == null) {
			return message(source, "Worldbound is not ready yet.");
		}
		quests.pause(source.getServer(), !state.paused);
		return message(source, state.paused ? "Worldbound paused." : "Worldbound resumed.");
	}

	private static int submit(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		QuestManager quests = ServerEventHandlers.quests();
		if (state == null || quests == null || source.getPlayer() == null) {
			return message(source, "Run this as a player after Worldbound is ready.");
		}
		return QuestTurnIn.submit(source.getServer(), source.getPlayer(), state, quests, true) ? 1 : 0;
	}

	private static int nextQuest(CommandSourceStack source) {
		ServerEventHandlers.quests().nextQuest(source.getServer());
		return message(source, "Advanced to next quest.");
	}

	private static int setQuest(CommandSourceStack source, String id) {
		boolean changed = ServerEventHandlers.quests().setQuest(source.getServer(), id);
		return message(source, changed ? "Set quest to " + id + "." : "Unknown quest id: " + id);
	}

	private static int expand(CommandSourceStack source, int blocks) {
		ServerEventHandlers.quests().expand(source.getServer(), blocks);
		return message(source, "Expanded border by " + blocks + " blocks.");
	}

	private static int rollPunishment(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		QuestManager quests = ServerEventHandlers.quests();
		if (state == null || quests == null || source.getPlayer() == null) {
			return message(source, "Run this as a player after Worldbound is ready.");
		}
		PunishmentWheel.apply(source.getServer(), state, quests.activeQuest(), source.getPlayer(), PunishmentWheel.roll());
		quests.saveAndSync(source.getServer());
		return message(source, "Punishment rolled.");
	}

	private static int spawnGuide(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		if (state == null) {
			return message(source, "Worldbound is not ready yet.");
		}
		GuideVillagerSpawner.spawn(source.getServer().overworld(), state);
		return message(source, "Spawned the Worldbound Guide at the border center.");
	}

	private static int resetRun(CommandSourceStack source) {
		WorldboundState state = ServerEventHandlers.state();
		QuestManager quests = ServerEventHandlers.quests();
		if (state == null || quests == null) {
			return message(source, "Worldbound is not ready yet.");
		}
		state.reset();
		Vec3 position = source.getPosition();
		BorderManager.centerOn(state, position.x(), position.z());
		state.started = true;
		state.paused = false;
		BorderManager.apply(source.getServer(), state);
		GuideVillagerSpawner.spawn(source.getServer().overworld(), state);
		quests.saveAndSync(source.getServer());
		return message(source, "Worldbound run reset and restarted.");
	}

	private static int message(CommandSourceStack source, String message) {
		source.sendSuccess(() -> Component.literal("[Worldbound] " + message), false);
		return 1;
	}

}
