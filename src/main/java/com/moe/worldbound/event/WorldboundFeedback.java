package com.moe.worldbound.event;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;

public final class WorldboundFeedback {
	private WorldboundFeedback() {
	}

	public static void questComplete(MinecraftServer server, String questTitle, int reward) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			title(player, "Quest Complete", questTitle + " | Border +" + reward);
			actionBar(player, "Worldbound expanded by " + reward + " blocks");
			sound(player, SoundEvents.PLAYER_LEVELUP, 0.9F, 1.2F);
			sound(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 1.0F);
		}
	}

	public static void punishment(MinecraftServer server, String message) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			title(player, "Punishment", message);
			sound(player, SoundEvents.WITHER_SPAWN, 0.7F, 1.25F);
		}
	}

	private static void title(ServerPlayer player, String title, String subtitle) {
		player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 45, 15));
		player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title)));
		player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
	}

	private static void actionBar(ServerPlayer player, String message) {
		player.connection.send(new ClientboundSystemChatPacket(Component.literal(message), true));
	}

	private static void sound(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
		player.connection.send(new ClientboundSoundPacket(
				BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
				SoundSource.MASTER,
				player.getX(),
				player.getY(),
				player.getZ(),
				volume,
				pitch,
				player.getRandom().nextLong()
		));
	}
}
