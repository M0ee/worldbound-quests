package com.moe.worldbound.quest;

import java.util.Map;

public interface QuestObjective {
	String type();

	String targetId();

	int targetCount();

	String describe();

	default boolean isComplete(int progress) {
		return progress >= targetCount();
	}

	default boolean matchesEvent(String eventType, String eventTargetId) {
		return type().equals(eventType) && targetId().equals(eventTargetId);
	}

	default int progressFromInventory(Map<String, Integer> inventory) {
		return inventory.getOrDefault(targetId(), 0);
	}

	default String targetLabel() {
		String id = targetId().startsWith("#") ? targetId().substring(1) : targetId();
		int namespaceSeparator = id.indexOf(':');
		String path = namespaceSeparator >= 0 ? id.substring(namespaceSeparator + 1) : id;
		return path.replace('_', ' ');
	}
}
