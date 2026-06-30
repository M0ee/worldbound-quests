package com.moe.worldbound;

import com.moe.worldbound.command.WorldboundCommand;
import com.moe.worldbound.config.WorldboundConfig;
import com.moe.worldbound.event.ServerEventHandlers;
import com.moe.worldbound.network.WorldboundNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldboundMod implements ModInitializer {
	public static final String MOD_ID = "worldbound";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static WorldboundConfig config;

	@Override
	public void onInitialize() {
		config = WorldboundConfig.load();
		WorldboundNetworking.registerPayloads();
		ServerEventHandlers.register();
		WorldboundCommand.register();
		LOGGER.info("Worldbound Quests ready.");
	}

	public static WorldboundConfig config() {
		return config;
	}
}
