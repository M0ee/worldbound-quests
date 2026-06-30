package com.moe.worldbound.quest;

import com.moe.worldbound.quest.objective.CraftItemObjective;
import com.moe.worldbound.quest.objective.EnterDimensionObjective;
import com.moe.worldbound.quest.objective.KillEntityObjective;
import com.moe.worldbound.quest.objective.ObtainItemObjective;
import com.moe.worldbound.quest.objective.ReachDayObjective;
import com.moe.worldbound.quest.objective.VisitBiomeObjective;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuestRegistry {
	private static final String QUEST_RESOURCE = "/data/worldbound/quests/story_quests.json";
	private static final List<QuestDefinition> STORY_QUESTS = loadStoryQuests();

	private QuestRegistry() {
	}

	public static List<QuestDefinition> storyQuests() {
		return STORY_QUESTS;
	}

	public static Optional<Integer> indexOf(String id) {
		for (int i = 0; i < STORY_QUESTS.size(); i++) {
			if (STORY_QUESTS.get(i).id().equals(id)) {
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}

	private static List<QuestDefinition> loadStoryQuests() {
		try (InputStream stream = QuestRegistry.class.getResourceAsStream(QUEST_RESOURCE)) {
			if (stream == null) {
				return createStoryQuests();
			}
			try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				List<QuestDefinition> quests = parseQuests(JsonParser.parseReader(reader).getAsJsonObject());
				return quests.isEmpty() ? createStoryQuests() : Collections.unmodifiableList(quests);
			}
		} catch (IOException | IllegalStateException exception) {
			return createStoryQuests();
		}
	}

	private static List<QuestDefinition> parseQuests(JsonObject root) {
		JsonArray entries = root.getAsJsonArray("quests");
		List<QuestDefinition> quests = new ArrayList<>();
		if (entries == null) {
			return quests;
		}
		for (int i = 0; i < entries.size(); i++) {
			JsonObject entry = entries.get(i).getAsJsonObject();
			JsonObject objective = entry.getAsJsonObject("objective");
			JsonObject reward = entry.getAsJsonObject("reward");
			quests.add(new QuestDefinition(
					entry.get("id").getAsString(),
					entry.get("title").getAsString(),
					entry.has("unlockDay") ? entry.get("unlockDay").getAsInt() : QuestChapter.valueOf(entry.get("chapter").getAsString()).startDay(),
					QuestChapter.valueOf(entry.get("chapter").getAsString()),
					objective(objective),
					new QuestReward(reward.get("borderExpansion").getAsInt())
			));
		}
		return quests;
	}

	private static QuestObjective objective(JsonObject json) {
		String type = json.get("type").getAsString();
		String target = json.get("target").getAsString();
		int count = json.get("count").getAsInt();
		return switch (type) {
			case "obtain_item" -> new ObtainItemObjective(target, count);
			case "craft_item" -> new CraftItemObjective(target, count);
			case "kill_entity" -> new KillEntityObjective(target, count);
			case "enter_dimension" -> new EnterDimensionObjective(target, count);
			case "reach_day" -> new ReachDayObjective(target, count);
			case "visit_biome" -> new VisitBiomeObjective(target, count);
			default -> throw new IllegalArgumentException("Unknown quest objective type: " + type);
		};
	}

	private static List<QuestDefinition> createStoryQuests() {
		List<QuestDefinition> quests = new ArrayList<>();
		quests.add(new QuestDefinition("first_logs", "First Logs", QuestChapter.SURVIVAL, new ObtainItemObjective("#minecraft:logs", 8), new QuestReward(12)));
		quests.add(new QuestDefinition("basic_table", "Build a Workbench", QuestChapter.SURVIVAL, new CraftItemObjective("minecraft:crafting_table", 1), new QuestReward(8)));
		quests.add(new QuestDefinition("sapling_cache", "Plant a Backup", QuestChapter.SURVIVAL, new ObtainItemObjective("#minecraft:saplings", 3), new QuestReward(10)));
		quests.add(new QuestDefinition("first_food", "Stock the Camp", QuestChapter.SURVIVAL, new ObtainItemObjective("#worldbound:early_foods", 6), new QuestReward(16)));
		quests.add(new QuestDefinition("monster_watch", "Clear the First Night", QuestChapter.SURVIVAL, new KillEntityObjective("minecraft:zombie", 3), new QuestReward(20)));
		quests.add(new QuestDefinition("stone_kit", "Stone Tool Kit", QuestChapter.SURVIVAL, new ObtainItemObjective("minecraft:cobblestone", 32), new QuestReward(18)));
		quests.add(new QuestDefinition("day_ten", "Survive to Day Ten", QuestChapter.SURVIVAL, new ReachDayObjective("minecraft:overworld", 10), new QuestReward(32)));

		quests.add(new QuestDefinition("farm_seed", "Plant the Future", QuestChapter.SETTLEMENT, new ObtainItemObjective("minecraft:wheat_seeds", 16), new QuestReward(64)));
		quests.add(new QuestDefinition("sleeping_bags", "Make Real Beds", QuestChapter.SETTLEMENT, new ObtainItemObjective("#minecraft:beds", 2), new QuestReward(80)));
		quests.add(new QuestDefinition("shield_wall", "Raise Shields", QuestChapter.SETTLEMENT, new CraftItemObjective("minecraft:shield", 2), new QuestReward(96)));
		quests.add(new QuestDefinition("iron_age", "Enter the Iron Age", QuestChapter.SETTLEMENT, new ObtainItemObjective("minecraft:iron_ingot", 24), new QuestReward(128)));
		quests.add(new QuestDefinition("spider_string", "Secure String", QuestChapter.SETTLEMENT, new ObtainItemObjective("minecraft:string", 12), new QuestReward(128)));
		quests.add(new QuestDefinition("deep_mining", "Find Diamonds", QuestChapter.SETTLEMENT, new ObtainItemObjective("minecraft:diamond", 3), new QuestReward(192)));

		quests.add(new QuestDefinition("lava_bucket", "Bottle the Heat", QuestChapter.NETHER_PREP, new ObtainItemObjective("minecraft:lava_bucket", 1), new QuestReward(384)));
		quests.add(new QuestDefinition("obsidian_gate", "Build the Gate", QuestChapter.NETHER_PREP, new ObtainItemObjective("minecraft:obsidian", 10), new QuestReward(512)));
		quests.add(new QuestDefinition("nether_entry", "Step into the Nether", QuestChapter.NETHER_PREP, new EnterDimensionObjective("minecraft:the_nether", 1), new QuestReward(768)));
		quests.add(new QuestDefinition("fortress_scout", "Scout a Fortress", QuestChapter.NETHER_PREP, new KillEntityObjective("minecraft:wither_skeleton", 1), new QuestReward(768)));

		quests.add(new QuestDefinition("blaze_rods", "Gather Blaze Rods", QuestChapter.BLAZE_AND_PEARLS, new ObtainItemObjective("minecraft:blaze_rod", 8), new QuestReward(900)));
		quests.add(new QuestDefinition("ender_pearls", "Trade for Eyes", QuestChapter.BLAZE_AND_PEARLS, new ObtainItemObjective("minecraft:ender_pearl", 12), new QuestReward(1000)));
		quests.add(new QuestDefinition("enchanting", "Open the Enchanting Table", QuestChapter.BLAZE_AND_PEARLS, new CraftItemObjective("minecraft:enchanting_table", 1), new QuestReward(1200)));
		quests.add(new QuestDefinition("ancient_route", "Find a Warped Forest", QuestChapter.BLAZE_AND_PEARLS, new VisitBiomeObjective("minecraft:warped_forest", 1), new QuestReward(1200)));

		quests.add(new QuestDefinition("eyes_ready", "Prepare the Eyes", QuestChapter.DRAGON, new ObtainItemObjective("minecraft:ender_eye", 12), new QuestReward(1400)));
		quests.add(new QuestDefinition("stronghold_path", "Open the Stronghold", QuestChapter.DRAGON, new VisitBiomeObjective("minecraft:deep_dark", 1), new QuestReward(1600)));
		quests.add(new QuestDefinition("end_entry", "Enter the End", QuestChapter.DRAGON, new EnterDimensionObjective("minecraft:the_end", 1), new QuestReward(1800)));
		quests.add(new QuestDefinition("dragonfall", "Defeat the Ender Dragon", QuestChapter.DRAGON, new KillEntityObjective("minecraft:ender_dragon", 1), new QuestReward(3000)));
		return Collections.unmodifiableList(quests);
	}
}
