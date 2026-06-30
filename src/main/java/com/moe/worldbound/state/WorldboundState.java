package com.moe.worldbound.state;

import com.moe.worldbound.WorldboundMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WorldboundState {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String SAVE_FILE = "worldbound-state.json";

	public boolean started;
	public boolean paused;
	public int activeQuestIndex;
	public int activeQuestProgress;
	public double borderDiameter;
	public double borderCenterX;
	public double borderCenterZ;
	public int campaignDay;
	public double nextRewardMultiplier = 1.0;
	public String lastPunishment = "";

	public static WorldboundState fresh() {
		WorldboundState state = new WorldboundState();
		state.borderDiameter = WorldboundMod.config().startingBorderDiameter;
		return state;
	}

	public static WorldboundState load(MinecraftServer server) {
		Path path = savePath(server);
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				WorldboundState state = GSON.fromJson(reader, WorldboundState.class);
				if (state != null) {
					state.sanitize();
					return state;
				}
			} catch (IOException exception) {
				WorldboundMod.LOGGER.warn("Could not load Worldbound state, creating a new run.", exception);
			}
		}
		return fresh();
	}

	public void save(MinecraftServer server) {
		Path path = savePath(server);
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException exception) {
			WorldboundMod.LOGGER.warn("Could not save Worldbound state.", exception);
		}
	}

	public void reset() {
		started = false;
		paused = false;
		activeQuestIndex = 0;
		activeQuestProgress = 0;
		borderDiameter = WorldboundMod.config().startingBorderDiameter;
		borderCenterX = 0.0;
		borderCenterZ = 0.0;
		campaignDay = 0;
		nextRewardMultiplier = 1.0;
		lastPunishment = "";
	}

	private void sanitize() {
		activeQuestIndex = Math.max(0, activeQuestIndex);
		activeQuestProgress = Math.max(0, activeQuestProgress);
		borderDiameter = Math.max(1.0, borderDiameter);
		campaignDay = Math.max(0, campaignDay);
		nextRewardMultiplier = Math.max(0.1, nextRewardMultiplier);
	}

	private static Path savePath(MinecraftServer server) {
		return server.getWorldPath(LevelResource.ROOT).resolve(SAVE_FILE);
	}
}
