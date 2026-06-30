# Worldbound Quests: 50-Quest Route Plan

Generated with Gemini CLI on 2026-06-16, then adapted for the current Worldbound Quests objective system.

## Design Notes

- The campaign is paced for a two-player YouTube survival series with visible progression every few quests.
- Quest difficulty rises over time: early survival, settlement, mining, Nether entry, fortress/potion prep, End preparation, then dragon fight.
- Day gates create episode beats and prevent rushing too far ahead just because one easy objective appears early.
- Quest 50 is the Ender Dragon kill and unlocks on day 50.
- The plan uses only objective types currently supported by the mod: `obtain_item`, `craft_item`, `kill_entity`, `enter_dimension`, `visit_biome`, and `reach_day`.
- Some targets depend on current Minecraft mechanics, especially Nether fortress progression, Ender Eye crafting, stronghold discovery, and biome detection.

## Quest Table

| # | ID | Title | Unlock Day | Objective Type | Target | Count | Border Reward | Reason |
|---:|---|---|---:|---|---|---:|---:|---|
| 1 | `q01_arrival` | The Arrival | 1 | `reach_day` | `minecraft:overworld` | 1 | 20 | Starts the shared run cleanly. |
| 2 | `q02_punching_wood` | Punching Wood | 1 | `obtain_item` | `#minecraft:logs` | 16 | 24 | First essential resource. |
| 3 | `q03_work_bench` | Work Bench | 1 | `craft_item` | `minecraft:crafting_table` | 1 | 16 | Unlocks crafting. |
| 4 | `q04_rock_bottom` | Rock Bottom | 1 | `obtain_item` | `minecraft:cobblestone` | 32 | 28 | Stone-tier tools. |
| 5 | `q05_week_one_survival` | Week One Survival | 5 | `reach_day` | `minecraft:overworld` | 5 | 40 | First day gate. |
| 6 | `q06_soft_landing` | Soft Landing | 5 | `obtain_item` | `#minecraft:wool` | 6 | 36 | Beds for both players. |
| 7 | `q07_stock_the_larder` | Stock the Larder | 5 | `obtain_item` | `#worldbound:early_foods` | 24 | 32 | Food security. |
| 8 | `q08_night_watch` | Night Watch | 5 | `kill_entity` | `minecraft:zombie` | 10 | 40 | Early combat milestone. |
| 9 | `q09_orchard_backup` | Orchard Backup | 5 | `obtain_item` | `#minecraft:saplings` | 5 | 20 | Renewable wood. |
| 10 | `q10_double_digits` | Double Digits | 10 | `reach_day` | `minecraft:overworld` | 10 | 60 | Day 10 transition. |
| 11 | `q11_strike_the_iron` | Strike the Iron | 10 | `obtain_item` | `minecraft:iron_ingot` | 16 | 64 | Iron age starts. |
| 12 | `q12_raise_the_wall` | Raise the Wall | 10 | `craft_item` | `minecraft:shield` | 2 | 48 | Shields for both players. |
| 13 | `q13_fueling_the_fire` | Fueling the Fire | 10 | `obtain_item` | `minecraft:coal` | 64 | 40 | Torches and smelting. |
| 14 | `q14_bone_collector` | Bone Collector | 10 | `kill_entity` | `minecraft:skeleton` | 10 | 44 | Combat plus farming support. |
| 15 | `q15_fortnight_mark` | Fortnight Mark | 15 | `reach_day` | `minecraft:overworld` | 15 | 80 | Mid-early gate. |
| 16 | `q16_golden_prospects` | Golden Prospects | 15 | `obtain_item` | `minecraft:gold_ingot` | 12 | 72 | Bartering and utility. |
| 17 | `q17_liquid_logistics` | Liquid Logistics | 15 | `craft_item` | `minecraft:bucket` | 2 | 40 | Lava/water handling. |
| 18 | `q18_desert_scout` | Desert Scout | 15 | `visit_biome` | `minecraft:desert` | 1 | 100 | Exploration beat. |
| 19 | `q19_silicate_harvest` | Silicate Harvest | 15 | `obtain_item` | `minecraft:sand` | 64 | 50 | Glass and potion prep. |
| 20 | `q20_score_twenty` | Score Twenty | 20 | `reach_day` | `minecraft:overworld` | 20 | 120 | Nether push begins. |
| 21 | `q21_hidden_gems` | Hidden Gems | 20 | `obtain_item` | `minecraft:diamond` | 3 | 150 | Diamond pickaxe route. |
| 22 | `q22_deep_pressure` | Deep Pressure | 20 | `obtain_item` | `minecraft:obsidian` | 14 | 200 | Nether portal frame. |
| 23 | `q23_into_the_heat` | Into the Heat | 20 | `enter_dimension` | `minecraft:the_nether` | 1 | 400 | Major series shift. |
| 24 | `q24_piglin_tribute` | Piglin Tribute | 20 | `obtain_item` | `minecraft:gold_nugget` | 64 | 80 | Nether resource work. |
| 25 | `q25_quarter_century` | Quarter Century | 25 | `reach_day` | `minecraft:overworld` | 25 | 150 | Nether foothold gate. |
| 26 | `q26_balloon_pop` | Balloon Pop | 25 | `kill_entity` | `minecraft:ghast` | 1 | 180 | Video-friendly danger moment. |
| 27 | `q27_fortress_found` | Fortress Found | 25 | `kill_entity` | `minecraft:wither_skeleton` | 2 | 220 | Confirms fortress access. |
| 28 | `q28_rod_of_power` | Rod of Power | 25 | `obtain_item` | `minecraft:blaze_rod` | 10 | 300 | Required for potions and eyes. |
| 29 | `q29_undergrowth` | Undergrowth | 25 | `obtain_item` | `minecraft:nether_wart` | 12 | 200 | Brewing foundation. |
| 30 | `q30_one_month_down` | One Month Down | 30 | `reach_day` | `minecraft:overworld` | 30 | 250 | Potion era gate. |
| 31 | `q31_master_alchemist` | Master Alchemist | 30 | `craft_item` | `minecraft:brewing_stand` | 1 | 150 | Brewing unlocked. |
| 32 | `q32_fire_resistance` | Fire Resistance | 30 | `obtain_item` | `minecraft:magma_cream` | 8 | 160 | Nether safety prep. |
| 33 | `q33_salty_tears` | Salty Tears | 30 | `obtain_item` | `minecraft:ghast_tear` | 2 | 350 | Rare potion ingredient. |
| 34 | `q34_shiny_fruit` | Shiny Fruit | 30 | `obtain_item` | `minecraft:glistering_melon_slice` | 16 | 140 | Healing potion ingredient. |
| 35 | `q35_the_long_haul` | The Long Haul | 35 | `reach_day` | `minecraft:overworld` | 35 | 300 | Late-game prep gate. |
| 36 | `q36_soul_harvester` | Soul Harvester | 35 | `obtain_item` | `minecraft:soul_sand` | 16 | 120 | Nether utility and prep. |
| 37 | `q37_pearlescent` | Pearlescent | 35 | `obtain_item` | `minecraft:ender_pearl` | 16 | 400 | Stronghold preparation. |
| 38 | `q38_fuel_of_eyes` | Fuel of Eyes | 35 | `obtain_item` | `minecraft:blaze_powder` | 16 | 200 | Eye crafting support. |
| 39 | `q39_all_seeing` | All-Seeing | 35 | `craft_item` | `minecraft:ender_eye` | 12 | 500 | Find/activate portal. |
| 40 | `q40_fortieth_sunset` | Fortieth Sunset | 40 | `reach_day` | `minecraft:overworld` | 40 | 400 | Stronghold hunt begins. |
| 41 | `q41_echoes_in_dark` | Echoes in Dark | 40 | `visit_biome` | `minecraft:deep_dark` | 1 | 600 | High-risk exploration. |
| 42 | `q42_nest_clearing` | Nest Clearing | 40 | `kill_entity` | `minecraft:silverfish` | 5 | 450 | Portal room confirmation. |
| 43 | `q43_ancient_scrap` | Ancient Scrap | 40 | `obtain_item` | `minecraft:ancient_debris` | 2 | 800 | Optional final upgrade challenge. |
| 44 | `q44_heavy_metal` | Heavy Metal | 40 | `craft_item` | `minecraft:anvil` | 1 | 200 | Repairs and enchant support. |
| 45 | `q45_final_countdown` | Final Countdown | 45 | `reach_day` | `minecraft:overworld` | 45 | 500 | Final prep gate. |
| 46 | `q46_notchs_favor` | Notch's Favor | 45 | `obtain_item` | `minecraft:golden_apple` | 6 | 400 | Boss fight survival. |
| 47 | `q47_rain_of_arrows` | Rain of Arrows | 45 | `obtain_item` | `minecraft:arrow` | 128 | 200 | Crystal fight prep. |
| 48 | `q48_the_void_calls` | The Void Calls | 45 | `enter_dimension` | `minecraft:the_end` | 1 | 1200 | Final arena entry. |
| 49 | `q49_judgment_day` | Judgment Day | 50 | `reach_day` | `minecraft:overworld` | 50 | 1000 | Final time gate. |
| 50 | `q50_dragonfall` | Dragonfall | 50 | `kill_entity` | `minecraft:ender_dragon` | 1 | 5000 | Series finale. |
