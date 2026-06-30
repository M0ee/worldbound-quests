package com.moe.worldbound.quest;

import com.moe.worldbound.WorldboundMod;
import com.moe.worldbound.border.BorderManager;
import com.moe.worldbound.event.WorldboundFeedback;
import com.moe.worldbound.network.WorldboundNetworking;
import com.moe.worldbound.state.WorldboundState;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QuestManager {
	private final WorldboundState state;

	public QuestManager(WorldboundState state) {
		this.state = state;
	}

	public QuestDefinition activeQuest() {
		List<QuestDefinition> quests = QuestRegistry.storyQuests();
		int index = Math.min(state.activeQuestIndex, quests.size() - 1);
		return quests.get(index);
	}

	public boolean start(MinecraftServer server, double centerX, double centerZ) {
		if (state.started) {
			return false;
		}
		state.started = true;
		state.paused = false;
		BorderManager.centerOn(state, centerX, centerZ);
		BorderManager.apply(server, state);
		saveAndSync(server);
		return true;
	}

	public boolean pause(MinecraftServer server, boolean paused) {
		if (state.paused == paused) {
			return false;
		}
		state.paused = paused;
		saveAndSync(server);
		return true;
	}

	public boolean setQuest(MinecraftServer server, String id) {
		Optional<Integer> index = QuestRegistry.indexOf(id);
		if (index.isEmpty()) {
			return false;
		}
		state.activeQuestIndex = index.get();
		state.activeQuestProgress = 0;
		saveAndSync(server);
		return true;
	}

	public void nextQuest(MinecraftServer server) {
		advance(server);
	}

	public boolean recordEvent(MinecraftServer server, String type, String targetId, int amount) {
		if (!canProgress()) {
			return false;
		}
		QuestDefinition quest = activeQuest();
		if (!quest.objective().matchesEvent(type, targetId)) {
			return false;
		}
		state.activeQuestProgress += Math.max(1, amount);
		if (quest.objective().isComplete(state.activeQuestProgress)) {
			complete(server, quest);
		} else {
			saveAndSync(server);
		}
		return true;
	}

	public boolean recordInventory(MinecraftServer server, Map<String, Integer> inventory) {
		if (!canProgress()) {
			return false;
		}
		QuestDefinition quest = activeQuest();
		String type = quest.objective().type();
		if (!type.equals("obtain_item") && !type.equals("craft_item")) {
			return false;
		}
		int amount = quest.objective().progressFromInventory(inventory);
		if (amount <= state.activeQuestProgress) {
			return false;
		}
		state.activeQuestProgress = Math.min(amount, quest.objective().targetCount());
		if (!type.equals("obtain_item") && quest.objective().isComplete(state.activeQuestProgress)) {
			complete(server, quest);
		} else {
			saveAndSync(server);
		}
		return true;
	}

	public boolean submit(MinecraftServer server) {
		if (!canProgress()) {
			return false;
		}
		QuestDefinition quest = activeQuest();
		if (!quest.objective().type().equals("obtain_item") || !quest.objective().isComplete(state.activeQuestProgress)) {
			return false;
		}
		complete(server, quest);
		return true;
	}

	public boolean recordDay(MinecraftServer server, int day) {
		state.campaignDay = Math.max(state.campaignDay, day);
		QuestDefinition quest = activeQuest();
		if (canProgress() && quest.objective().type().equals("reach_day") && day >= quest.objective().targetCount()) {
			state.activeQuestProgress = quest.objective().targetCount();
			complete(server, quest);
			return true;
		}
		saveAndSync(server);
		return false;
	}

	public void expand(MinecraftServer server, int blocks) {
		BorderManager.expand(state, blocks);
		BorderManager.apply(server, state);
		saveAndSync(server);
	}

	public void saveAndSync(MinecraftServer server) {
		state.save(server);
		WorldboundNetworking.syncAll(server, state, activeQuest());
	}

	private boolean canProgress() {
		return state.started && !state.paused && state.activeQuestIndex < QuestRegistry.storyQuests().size() && activeQuest().isUnlocked(state.campaignDay);
	}

	private void complete(MinecraftServer server, QuestDefinition quest) {
		int reward = (int) Math.round(quest.reward().borderExpansion() * WorldboundMod.config().expansionMultiplier * state.nextRewardMultiplier);
		BorderManager.expand(state, reward);
		state.nextRewardMultiplier = 1.0;
		WorldboundFeedback.questComplete(server, quest.title(), reward);
		advance(server);
	}

	private void advance(MinecraftServer server) {
		if (state.activeQuestIndex < QuestRegistry.storyQuests().size() - 1) {
			state.activeQuestIndex++;
			state.activeQuestProgress = 0;
		} else {
			state.activeQuestProgress = activeQuest().objective().targetCount();
		}
		BorderManager.apply(server, state);
		saveAndSync(server);
	}
}
