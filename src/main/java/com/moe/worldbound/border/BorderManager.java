package com.moe.worldbound.border;

import com.moe.worldbound.quest.QuestChapter;
import com.moe.worldbound.state.WorldboundState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;

public final class BorderManager {
	private BorderManager() {
	}

	public static double expand(WorldboundState state, double blocks) {
		state.borderDiameter = Math.max(1.0, state.borderDiameter + Math.max(0.0, blocks));
		return state.borderDiameter;
	}

	public static double shrink(WorldboundState state, double blocks, QuestChapter chapter) {
		double minimum = Math.min(state.borderDiameter, chapter.minimumBorder());
		state.borderDiameter = Math.max(minimum, state.borderDiameter - Math.max(0.0, blocks));
		return state.borderDiameter;
	}

	public static void centerOn(WorldboundState state, double x, double z) {
		state.borderCenterX = x;
		state.borderCenterZ = z;
	}

	public static void apply(MinecraftServer server, WorldboundState state) {
		WorldBorder border = server.overworld().getWorldBorder();
		border.setCenter(state.borderCenterX, state.borderCenterZ);
		border.setSize(state.borderDiameter);
	}
}
