package dk.mosberg.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dk.mosberg.Alchemy;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Loads beverage definitions from data/alchemy/alcohol/*.json and container definitions from
 * data/alchemy/containers/*.json. All data is loaded during mod initialization and validated before
 * being registered.
 */
public final class DataLoader {
    private static final Gson GSON = new Gson();
    private static final String ALCOHOL_PATH = "data/alchemy/beverages/";
    private static final String CONTAINER_PATH = "data/alchemy/containers/";

    private DataLoader() {}

    /**
     * Load all alcohol definitions from classpath resources.
     */
    public static void loadBeverages() {
        String[] containerIds = {"aluminum_can", "aluminum_keg"};

        String[] beerIds = {"coppercap_lager", "frostmarsh_pils", "emberhold_amber_ale",
                "sunvale_golden_pale", "stormwake_session_ipa", "blackvault_stout",
                "thornveil_herbal_ale", "ropesend_dockside_brew"};

        String[] spiritIds = {"chorus_bloom_gin", "dune_mirage_rum", "frostpetal_schnapps",
                "hollowshade_absinthe", "scarabgold_brandy", "soulflame_spirit"};

        for (String id : containerIds) {
            try {
                loadContainer(id);
            } catch (Exception e) {
                Alchemy.LOGGER.error("Failed to load container definition: {}", id, e);
            }
        }

        for (String id : beerIds) {
            try {
                loadBeverage("beer/" + id);
            } catch (Exception e) {
                Alchemy.LOGGER.error("Failed to load beverage definition: {}", id, e);
            }
        }

        for (String id : spiritIds) {
            try {
                loadBeverage("spirit/" + id);
            } catch (Exception e) {
                Alchemy.LOGGER.error("Failed to load beverage definition: {}", id, e);
            }
        }
    }

    private static void loadBeverage(String path) throws IOException {
        String resourcePath = ALCOHOL_PATH + path + ".json";

        try (InputStream input =
                DataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            try (JsonReader reader =
                    new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                parseBeverageJson(root);
            }
        }
    }

    private static void parseBeverageJson(JsonObject root) {
        validateJsonField(root, "type");
        String type = root.get("type").getAsString();
        if (!"alchemy:alcohol".equals(type)) {
            throw new IllegalArgumentException("Invalid beverage type: " + type);
        }

        validateJsonField(root, "id");
        Identifier id = Identifier.of(root.get("id").getAsString());

        validateJsonField(root, "stack_size");
        int stackSize = root.get("stack_size").getAsInt();

        // Extract primary effect (first effect in list)
        validateJsonField(root, "effects");
        JsonArray effectsArray = root.getAsJsonArray("effects");
        if (effectsArray.isEmpty()) {
            throw new IllegalArgumentException("No effects defined for beverage: " + id);
        }

        JsonObject primaryEffect = effectsArray.get(0).getAsJsonObject();
        validateJsonField(primaryEffect, "effect");
        validateJsonField(primaryEffect, "duration");
        validateJsonField(primaryEffect, "amplifier");

        RegistryEntry<StatusEffect> effect = Registries.STATUS_EFFECT
                .getEntry(Identifier.of(primaryEffect.get("effect").getAsString()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown status effect: " + primaryEffect.get("effect").getAsString()));

        int durationTicks = primaryEffect.get("duration").getAsInt();
        int amplifier = primaryEffect.get("amplifier").getAsInt();

        // Use fixed hunger/saturation values for all beverages
        int hunger = 4;
        float saturation = 0.6F;

        BeverageManager.register(id, effect, durationTicks, amplifier, hunger, saturation);
        Alchemy.LOGGER.info("Loaded beverage definition: {} (stack size: {})", id, stackSize);
    }

    private static void validateJsonField(JsonObject json, String fieldName) {
        if (!json.has(fieldName)) {
            throw new IllegalArgumentException(
                    "Missing required field '" + fieldName + "' in beverage definition");
        }
    }

    /**
     * Load container definitions from data/alchemy/containers/*.json. Currently a placeholder for
     * future container system expansion.
     */
    /**
     * Load container definitions from data/alchemy/containers/*.json.
     */
    public static void loadContainers() {
        String[] containers = {"aluminum_can", "aluminum_keg"};
        for (String name : containers) {
            try {
                loadContainer(name);
            } catch (Exception e) {
                Alchemy.LOGGER.error("Failed to load container definition: {}", name, e);
            }
        }
    }

    private static void loadContainer(String name) throws IOException {
        String resourcePath = CONTAINER_PATH + name + ".json";

        try (InputStream input =
                DataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                Alchemy.LOGGER.warn("Container definition not found: {}", resourcePath);
                return;
            }

            try (JsonReader reader =
                    new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                parseContainerJson(root);
            }
        }
    }

    private static void parseContainerJson(JsonObject root) {
        validateJsonField(root, "type");
        String type = root.get("type").getAsString();
        if (!"alchemy:container".equals(type)) {
            throw new IllegalArgumentException("Invalid container type: " + type);
        }

        validateJsonField(root, "id");
        Identifier id = Identifier.of(root.get("id").getAsString());

        validateJsonField(root, "stack_size");
        int stackSize = root.get("stack_size").getAsInt();

        boolean breakable = root.has("breakable") && root.get("breakable").getAsBoolean();
        boolean returnsContainer =
                root.has("returns_container") && root.get("returns_container").getAsBoolean();

        Alchemy.LOGGER.info(
                "Loaded container definition: {} (stack size: {}, breakable: {}, returns: {})", id,
                stackSize, breakable, returnsContainer);

        // Container data validated; extend ContainerManager when implementing container system
    }
}
