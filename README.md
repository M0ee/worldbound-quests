# Worldbound Quests

Worldbound Quests is a Fabric mod for Minecraft Java 26.1.2. It runs a shared co-op campaign where everyone starts inside a tiny world border, completes one day-gated story quest at a time, expands the playable world, and faces a weighted punishment roll on death.

Created by MoeQuack, known in-game as UncleMoee.

## Requirements

- Minecraft Java 26.1.2
- Fabric Loader 0.19.2
- Fabric API 0.150.0+26.1.2
- Java 25 for development and running Gradle

## Development

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

Fabric 26.1 uses Mojang official names and the `net.fabricmc.fabric-loom` plugin. This project intentionally uses normal `implementation` dependencies instead of old remapping-specific dependency buckets.

## LAN Setup

1. Install Fabric Loader 0.19.2 for Minecraft 26.1.2.
2. Put Fabric API and the Worldbound Quests jar in each player's `mods` folder. Every player needs the mod for the HUD and quest book.
3. Start a singleplayer world and open it to LAN.
4. Run `/worldbound start` as an operator or with cheats enabled.

## Dedicated Server Setup

1. Install a Fabric dedicated server for Minecraft 26.1.2.
2. Put Fabric API and the Worldbound Quests jar in the server `mods` folder.
3. Put the same jars in each client `mods` folder. Every player needs the mod for the shared client UI.
4. Start the server, then run `/worldbound start`.

## Client UI

- The HUD shows the active quest, progress, Minecraft day, border size, and expanded amount.
- Press `K` to open the Worldbound quest book.
- The quest book has 50 quests, day locks, chapter labels, a scrollbar, and keyboard/mouse scrolling.
- Scroll with the mouse wheel, Up/Down, or Page Up/Page Down.

## Commands

All commands require operator/admin permission.

- `/worldbound status`
- `/worldbound start`
- `/worldbound pause`
- `/worldbound nextquest`
- `/worldbound setquest <id>`
- `/worldbound expand <blocks>`
- `/worldbound rollpunishment`
- `/worldbound resetrun`

## Config

On first launch the mod creates `config/worldbound.json`.

- `startingBorderDiameter`: default `32`
- `expansionMultiplier`: default `1.0`
- `deathPunishments`: default `true`
- `punishmentSeverity`: default `1.0`
- `campaignDayTarget`: default `100`
- `hudVisible`: default `true`

## Punishments

Death punishments are weighted and temporary. The default wheel includes small border shrink, slowness, mining fatigue, hunger, weakness, blindness, darkness, food drain, and reduced next expansion. It intentionally avoids item deletion and XP drain for recording-friendly multiplayer.

## License

Worldbound Quests is owned by MoeQuack and uses a custom source-available license: `LicenseRef-MoeQuack-Source-Available`. You may play it and record/stream/upload/monetize gameplay without crediting or linking the mod, but you may not reupload, redistribute, sell, publish modified builds, remove credits from the mod files, or claim the mod as your own without written permission.

## Current Scope

The first release focuses on the built-in 50-quest day-gated story campaign. A visual quest editor and custom campaign loader are good v2 targets.
