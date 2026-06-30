package com.moe.worldbound.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WorldboundConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public int startingBorderDiameter = 32;
	public double expansionMultiplier = 1.0;
	public boolean deathPunishments = true;
	public double punishmentSeverity = 1.0;
	public int campaignDayTarget = 100;
	public boolean hudVisible = true;

	public static WorldboundConfig load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("worldbound.json");
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				WorldboundConfig loaded = GSON.fromJson(reader, WorldboundConfig.class);
				if (loaded != null) {
					loaded.sanitize();
					return loaded;
				}
			} catch (IOException ignored) {
			}
		}

		WorldboundConfig fallback = new WorldboundConfig();
		fallback.save(path);
		return fallback;
	}

	public void sanitize() {
		startingBorderDiameter = Math.max(16, startingBorderDiameter);
		expansionMultiplier = Math.max(0.1, expansionMultiplier);
		punishmentSeverity = Math.max(0.0, punishmentSeverity);
		campaignDayTarget = Math.max(20, campaignDayTarget);
	}

	private void save(Path path) {
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException ignored) {
		}
	}
}
