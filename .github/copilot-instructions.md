# Alchemy - AI Coding Agent Instructions

## Project overview

Alchemy is a Minecraft **1.21.11** Fabric mod that provides a data-driven, extensible alcohol and brewing system, enhancing the vanilla potion-making experience. It is built using Java 21 and Yarn mappings, employing a split source set architecture for clear separation between client and server code.
Mod metadata (id/version/name/description/links) is defined in `gradle.properties` and expanded into `fabric.mod.json` during `processResources`, so `fabric.mod.json` should be treated as a template output—not the source of truth.

## Remote indexing & references

This project benefits from Copilot “remote indexing” of upstream APIs so suggestions match Fabric/Yarn conventions for the target versions used by the build.
Index these repositories for implementation patterns and API usage:

- Fabric API: https://github.com/FabricMC/fabric
- Yarn mappings: https://github.com/FabricMC/yarn
- Fabric Loader: https://github.com/FabricMC/fabric-loader

Useful version-pinned Javadocs (match the versions in `gradle.properties`):

- Yarn **1.21.11+build.4**: https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/
- Fabric API **0.141.1+1.21.11**: https://maven.fabricmc.net/docs/fabric-api-0.141.1+1.21.11/

## Architecture rules (split sources)

The project utilizes Loom’s `splitEnvironmentSourceSets()` to maintain a clear separation between client-only and shared code. Client-only classes are located in `src/client/java`, while shared logic resides in `src/main/java`. This separation prevents server crashes due to client-specific imports.
Entrypoints are:

- `dk.mosberg.Alchemy` (main / common)
- `dk.mosberg.client.AlchemyClient` (client-only)
- `dk.mosberg.client.datagen.AlchemyDataGenerator` (data generation)

Hard rules:

- Server/common code MUST NOT import anything from `dk.mosberg.client.*` (will crash dedicated servers).
- Client code MAY import common code (clients ship all classes).
- Keep initialization side-correct: register gameplay logic in `Alchemy#onInitialize`, and rendering/visual hooks in `AlchemyClient#onInitializeClient`.

## Build & workflows (Gradle/Loom)

The primary build and run configurations are managed by Loom, with separate directories for client (`run/`) and server (`run-server/`). Key Gradle tasks include:

- **Run Client:** `./gradlew runClient` (or use the “Minecraft Client” IDE configuration).
- **Run Server:** `./gradlew runServer` (or use the “Minecraft Server” IDE configuration).
- **Data Generation:** `./gradlew runDatagen` generates resources in `src/main/generated/resources`.
- **Build JAR:** `./gradlew build` processes resources and packages the mod.
- **Project Diagnostics:** `./gradlew projectInfo` provides build information and version details.
  Use these tasks/flows:

- Client dev: `./gradlew runClient` (or “Minecraft Client” IDE run config).
- Dedicated server dev: `./gradlew runServer` (or “Minecraft Server” IDE run config).
- Data generation: `./gradlew runDatagen`, output goes to `src/main/generated/resources`, which is included as a resource source directory.
- Build JAR: `./gradlew build` (includes resource processing + packaging).
- Project diagnostics: `./gradlew projectInfo` prints the resolved versions and build info.

## Coding conventions & expectations

For coding conventions, utilize `Alchemy.MOD_ID` for namespacing and `Alchemy.LOGGER` for logging. Ensure log messages are actionable and provide sufficient context for debugging data-driven inputs. The project emphasizes small, composable systems, advocating for a clear separation of concerns in data parsing, mapping, and presentation.
Prefer small, composable systems: parse/validate data (e.g., JSON) in one layer, map it to runtime registries in another, and keep side-specific presentation (tooltips/overlays/renderers) in client-only code.
Dependencies and constraints to respect:

- Minecraft + Fabric Loader + Fabric API versions must match `gradle.properties` (`minecraft_version`, `loader_version`, `fabric_version`, `yarn_mappings`).
- GSON is bundled into the jar via Gradle (`include implementation`), so it’s safe for runtime JSON parsing.
- The Java toolchain targets Java 21 (`options.release` + toolchain config), so use modern Java 21 language features when they improve clarity.

## “Do / Don’t” for Copilot changes

Do:

- Keep common code in `src/main/java` and client-only code in `src/client/java`, mirroring the split-sources contract.
- Update metadata in `gradle.properties` (not `fabric.mod.json`) when changing mod name/version/links.
- When adding new data-driven content, also add validation (fail fast with clear logs) and consider datagen to keep assets consistent.

Don’t:

- Don’t add client imports into common/server packages.
- Don’t hardcode values already defined as Gradle properties (versions, mod id, etc.).
- Don’t “fix” generated resources by editing outputs under `build/`—treat `src/main/generated/resources` as the committed/generated source of truth when used.

## Performance & build hygiene

The Gradle configuration is optimized for performance, allocating 4GB of heap memory and utilizing G1GC for garbage collection. It supports configuration caching, parallel builds, and build caching to enhance build times. Avoid tasks that compromise configuration-cache compatibility unless absolutely necessary.
If adding new Gradle logic, keep it configuration-cache friendly (avoid reading environment/state at configuration time; capture values then use them in `doLast`).
When adding dependencies, prefer implementation over api to limit the exposure of transitive dependencies and improve build times.
