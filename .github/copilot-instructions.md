# Alchemy – AI Coding Agent Guide

## Remote indexing & references

This project benefits from Copilot “remote indexing” of upstream APIs so suggestions match Fabric/Yarn conventions for the target versions used by the build.
Index these repositories for implementation patterns and API usage:

- Fabric API: https://github.com/FabricMC/fabric
- Yarn mappings: https://github.com/FabricMC/yarn
- Fabric Loader: https://github.com/FabricMC/fabric-loader

Useful version-pinned Javadocs (match the versions in `gradle.properties`):

- Yarn **1.21.11+build.4**: https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/
- Fabric API **0.141.1+1.21.11**: https://maven.fabricmc.net/docs/fabric-api-0.141.1+1.21.11/

- Scope: Fabric mod for Minecraft 1.21.11, Java 21 toolchain, Yarn 1.21.11+build.4, Fabric API 0.141.1+1.21.11. Versions live in `gradle.properties`; keep them in sync.
- Entrypoints: `dk.mosberg.Alchemy` (common), `dk.mosberg.client.AlchemyClient` (client), `dk.mosberg.client.datagen.AlchemyDataGenerator` (datagen). Wire gameplay in `onInitialize`, client visuals in `onInitializeClient`.
- Split sources: Loom `splitEnvironmentSourceSets()` puts common code in `src/main/java` and client-only code in `src/client/java`. Never import `dk.mosberg.client.*` from common to avoid dedicated-server crashes.
- Mod metadata: `gradle.properties` is the source of truth for mod id/name/version/links; `src/main/resources/fabric.mod.json` is generated during `processResources` and should not be hand-edited.
- Logging and ids: reuse `Alchemy.MOD_ID` for registry namespacing and `Alchemy.LOGGER` for diagnostics; write actionable messages around data-driven inputs.
- Current code surface: `BeverageData`, `BeverageManager`, `BeverageEffectManager`, `BeverageCanItem`, `ModItems`, `ModItemGroups`, and `ModEffects` are stubs—extend these instead of creating parallel ad-hoc classes.
- Assets already present: canned beverages pack lives under `src/main/resources/assets/alchemy`.
  - `items/*.json` tie registry entries to models (see `items/coppercap_lager.json`).
  - `models/item/cans/*.json` plus `textures/item/cans/*` define visuals; keep model/texture pairs in sync for new cans.
  - `lang/en_us.json` carries display, tooltip, brew-time, ingredient, and warning strings; stick to `item.alchemy.<id>[.<suffix>]` keys.
- Data direction: prefer schema-driven loading + validation before registry writes; GSON is bundled (see `build.gradle` include) so runtime JSON parsing is safe.
- Datagen: `AlchemyDataGenerator` exists but is empty—add providers here when generating assets; outputs should land in `src/main/generated/resources`.
- Workflows: `./gradlew runClient`, `./gradlew runServer`, `./gradlew runDatagen`, `./gradlew build`, `./gradlew projectInfo`. Runtime dirs: `run/` (client), `run-server/` (server).
- Build hygiene: don’t touch `build/` outputs; commit generated assets from `src/main/generated/resources` only. Keep configuration-cache friendliness if editing Gradle (avoid work at configuration time).
- Remote references: when unsure about API use, check Fabric API, Yarn, and Fabric Loader repos plus pinned Javadocs for the exact versions above.
- Java style: lean on Java 21 features where they clarify intent; keep systems small and composable—parse/validate data, map to registries, then present client-side overlays/tooltips separately.
