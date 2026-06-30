package com.moe.worldbound.event;

import com.moe.worldbound.state.WorldboundState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;

public final class GuideVillagerSpawner {
	private GuideVillagerSpawner() {
	}

	public static void spawn(ServerLevel level, WorldboundState state) {
		removeExisting(level);
		BlockPos pos = BlockPos.containing(state.borderCenterX, level.getMaxY(), state.borderCenterZ);
		BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
		Villager villager = EntityType.VILLAGER.create(level, EntitySpawnReason.COMMAND);
		if (villager == null) {
			return;
		}
		villager.setCustomName(Component.literal("Worldbound Guide"));
		villager.setCustomNameVisible(true);
		villager.setPersistenceRequired();
		villager.setInvulnerable(true);
		villager.setNoAi(true);
		villager.setPos(ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5);
		level.addFreshEntity(villager);
	}

	private static void removeExisting(ServerLevel level) {
		for (net.minecraft.world.entity.Entity entity : level.getAllEntities()) {
			if (entity.hasCustomName() && "Worldbound Guide".equals(entity.getCustomName().getString())) {
				entity.discard();
			}
		}
	}
}
