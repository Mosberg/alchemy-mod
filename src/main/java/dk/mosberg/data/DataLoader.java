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
 * data/alchemy/containers/*.json.
 */
public final class DataLoader {
    private static final Gson GSON = new Gson();

    private DataLoader() {}

    /**
     * Load all alcohol definitions from classpath resources.
     */
    public static void loadBeverages() {
        String[] beverageIds = {"coppercap_lager", "frostmarsh_pils", "emberhold_amber_ale",
                "sunvale_golden_pale", "stormwake_session_ipa", "blackvault_stout",
                "thornveil_herbal_ale", "ropesend_dockside_brew"};

        for (String id : beverageIds) {
            try {
                loadBeverage(id);
            } catch (Exception e) {
                Alchemy.LOGGER.error("Failed to load beverage definition: {}", id, e);
            }
        }
    }

    private static void loadBeverage(String path) throws IOException {
        String resourcePath = "data/alchemy/alcohol/beer/" + path + ".json";
        InputStream input = DataLoader.class.getClassLoader().getResourceAsStream(resourcePath);

        if (input == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try (JsonReader reader =
                new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            parseBeverageJson(root);
        }
    }

    private static void parseBeverageJson(JsonObject root) {
        String type = root.get("type").getAsString();
        if (!"alchemy:alcohol".equals(type)) {
            throw new IllegalArgumentException("Invalid beverage type: " + type);
        }

        Identifier id = Identifier.of(root.get("id").getAsString());
        int stackSize = root.get("stack_size").getAsInt();

        // Extract primary effect (first effect in list)
        JsonArray effectsArray = root.getAsJsonArray("effects");
        if (effectsArray == null || effectsArray.size() == 0) {
            throw new IllegalArgumentException("No effects defined for beverage: " + id);
        }

        JsonObject primaryEffect = effectsArray.get(0).getAsJsonObject();
        RegistryEntry<StatusEffect> effect = Registries.STATUS_EFFECT
                .getEntry(Identifier.of(primaryEffect.get("effect").getAsString()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown status effect: " + primaryEffect.get("effect").getAsString()));

        int durationTicks = primaryEffect.get("duration").getAsInt();
        int amplifier = primaryEffect.get("amplifier").getAsInt();

        // For now, use fixed hunger/saturation; could be extended from JSON
        int hunger = 4;
        float saturation = 0.6F;

        BeverageData data =
                BeverageManager.register(id, effect, durationTicks, amplifier, hunger, saturation);
        Alchemy.LOGGER.info("Loaded beverage definition: {} (stack size: {})", id, stackSize);
    }

    /**
     * Load container definitions from data/alchemy/containers/*.json. Currently a placeholder for
     * future container system expansion.
     */
    public static void loadContainers() {
        try {
            String resourcePath = "data/alchemy/containers/aluminum_can.json";
            InputStream input = DataLoader.class.getClassLoader().getResourceAsStream(resourcePath);

            if (input == null) {
                Alchemy.LOGGER.warn("Container definition not found: {}", resourcePath);
                return;
            }

            try (JsonReader reader =
                    new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                parseContainerJson(root);
            }
        } catch (IOException e) {
            Alchemy.LOGGER.error("Failed to load container definitions", e);
        }
    }

    private static void parseContainerJson(JsonObject root) {
        String type = root.get("type").getAsString();
        if (!"alchemy:container".equals(type)) {
            throw new IllegalArgumentException("Invalid container type: " + type);
        }

        Identifier id = Identifier.of(root.get("id").getAsString());
        int stackSize = root.get("stack_size").getAsInt();
        boolean breakable = root.has("breakable") && root.get("breakable").getAsBoolean();
        boolean returnsContainer =
                root.has("returns_container") && root.get("returns_container").getAsBoolean();

        Alchemy.LOGGER.info(
                "Loaded container definition: {} (stack size: {}, breakable: {}, returns: {})", id,
                stackSize, breakable, returnsContainer);

        // Container data validated but not yet stored; extend ContainerManager when needed
    }
}
