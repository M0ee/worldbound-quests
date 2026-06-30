package com.moe.worldbound.quest;

import com.moe.worldbound.state.WorldboundState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class QuestTurnIn {
	private QuestTurnIn() {
	}

	public static boolean submit(MinecraftServer server, ServerPlayer player, WorldboundState state, QuestManager quests, boolean sendMessage) {
		if (state == null || quests == null) {
			return fail(player, sendMessage, "Worldbound is not ready yet.");
		}
		QuestDefinition quest = quests.activeQuest();
		if (!quest.objective().type().equals("obtain_item")) {
			return fail(player, sendMessage, "This quest completes by action, not item turn-in.");
		}
		if (countMatching(player.getInventory(), quest.objective().targetId()) < quest.objective().targetCount()) {
			return fail(player, sendMessage, "You do not have enough items to submit yet.");
		}
		removeMatching(player.getInventory(), quest.objective().targetId(), quest.objective().targetCount());
		state.activeQuestProgress = quest.objective().targetCount();
		quests.submit(server);
		if (sendMessage) {
			player.sendSystemMessage(Component.literal("[Worldbound] Submitted items for " + quest.title() + "."));
		}
		return true;
	}

	private static boolean fail(ServerPlayer player, boolean sendMessage, String message) {
		if (sendMessage) {
			player.sendSystemMessage(Component.literal("[Worldbound] " + message));
		}
		return false;
	}

	private static int countMatching(Inventory inventory, String targetId) {
		int count = 0;
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (matches(stack, targetId)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private static void removeMatching(Inventory inventory, String targetId, int amount) {
		int remaining = amount;
		for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (matches(stack, targetId)) {
				int removed = Math.min(remaining, stack.getCount());
				stack.shrink(removed);
				remaining -= removed;
			}
		}
	}

	private static boolean matches(ItemStack stack, String targetId) {
		if (stack.isEmpty()) {
			return false;
		}
		if (targetId.startsWith("#")) {
			return stack.is(tagKey(targetId.substring(1)));
		}
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(targetId);
	}

	private static TagKey<Item> tagKey(String id) {
		String[] parts = id.split(":", 2);
		String namespace = parts.length == 2 ? parts[0] : "minecraft";
		String path = parts.length == 2 ? parts[1] : parts[0];
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
