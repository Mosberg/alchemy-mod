package dk.mosberg.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.mosberg.Alchemy;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Discovers and parses all JSON definitions under data/alchemy/* at startup. Builds a ContentPack
 * that feeds item registration and runtime managers.
 */
public final class DataLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DATA_ROOT = "data/alchemy";
    private static final String BEVERAGES_DIR = DATA_ROOT + "/beverages";
    private static final String CONTAINERS_DIR = DATA_ROOT + "/containers";
    private static final String EQUIPMENT_DIR = DATA_ROOT + "/equipment";

    private DataLoader() {}

    public static ContentPack loadAll() {
        Map<Identifier, BeverageData> beverages = new LinkedHashMap<>();
        Map<Identifier, ContainerData> containers = new LinkedHashMap<>();
        Map<Identifier, EquipmentData> equipment = new LinkedHashMap<>();

        FabricLoader.getInstance().getModContainer(Alchemy.MOD_ID).ifPresentOrElse(container -> {
            for (Path root : container.getRootPaths()) {
                Path dataRoot = root.resolve(DATA_ROOT);
                if (!Files.exists(dataRoot)) {
                    continue;
                }
                try {
                    loadContainers(dataRoot.resolve("containers"), containers);
                    loadBeverages(dataRoot.resolve("beverages"), beverages, containers);
                    loadEquipment(dataRoot.resolve("equipment"), equipment);
                } catch (IOException e) {
                    Alchemy.LOGGER.error("Failed loading data from {}", dataRoot, e);
                }
            }
        }, () -> Alchemy.LOGGER.error("Missing mod container for {}", Alchemy.MOD_ID));

        Alchemy.LOGGER.info("Loaded {} beverages, {} containers, {} equipment entries",
                beverages.size(), containers.size(), equipment.size());
        return new ContentPack(beverages, containers, equipment);
    }

    private static void loadBeverages(Path beveragesPath, Map<Identifier, BeverageData> out,
            Map<Identifier, ContainerData> containers) throws IOException {
        if (!Files.isDirectory(beveragesPath)) {
            return;
        }

        try (var paths = Files.walk(beveragesPath)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (Reader reader =
                        new BufferedReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                    @SuppressWarnings("null")
                    JsonObject root = GSON.fromJson(reader, JsonObject.class);
                    BeverageData data = parseBeverage(root);
                    out.put(data.id(), data);
                } catch (Exception e) {
                    Alchemy.LOGGER.error("Failed to parse beverage JSON {}", path, e);
                }
            });
        }
    }

    private static BeverageData parseBeverage(JsonObject root) {
        expectType(root, "alchemy:alcohol");

        Identifier id = id(root, "id");
        Identifier container = id(root, "container");
        String rarity = string(root, "rarity", "common");
        int stackSize = integer(root, "stack_size", 16);

        JsonObject statsObj = object(root, "stats");
        double abv = number(statsObj, "alcohol_by_volume", 0);
        double strength = number(statsObj, "strength", 0);
        JsonObject nutritionObj = object(statsObj, "nutrition");
        int hunger = integer(nutritionObj, "hunger", 1);
        float saturation = (float) number(nutritionObj, "saturation", 0.0);
        JsonObject intoxObj = object(statsObj, "intoxication");
        double intoxValue = number(intoxObj, "value", 0);
        double intoxDecay = number(intoxObj, "decay_rate_per_tick", 0);

        BeverageData.Stats stats = new BeverageData.Stats(abv, strength,
                new BeverageData.Intoxication(intoxValue, intoxDecay),
                new BeverageData.Nutrition(hunger, saturation));

        JsonArray effectsArr = array(root, "effects");
        List<BeverageData.EffectEntry> effects = new ArrayList<>();
        for (JsonElement element : effectsArr) {
            JsonObject effectObj = element.getAsJsonObject();
            Identifier effectId = id(effectObj, "effect");
            RegistryEntry<net.minecraft.entity.effect.StatusEffect> effectEntry =
                    Registries.STATUS_EFFECT.getEntry(effectId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown status effect: " + effectId));
            int duration = integer(effectObj, "duration", 200);
            int amplifier = integer(effectObj, "amplifier", 0);
            float chance = (float) number(effectObj, "chance", 1.0);
            boolean showParticles = bool(effectObj, "show_particles", true);
            boolean showIcon = bool(effectObj, "show_icon", true);
            boolean ambient = bool(effectObj, "ambient", false);
            effects.add(new BeverageData.EffectEntry(effectEntry, duration, amplifier, chance,
                    showParticles, showIcon, ambient));
        }

        if (effects.isEmpty()) {
            throw new IllegalArgumentException("Beverage " + id + " defines no effects");
        }

        BeverageData.TextKeys textKeys = resolveTextKeys(root);
        BeverageData.Config config = parseConfig(root);

        return new BeverageData(id, string(root, "category", "beer"), string(root, "style", ""),
                container, rarity, stackSize, stats, effects, textKeys, config);
    }

    private static BeverageData.TextKeys resolveTextKeys(JsonObject root) {
        JsonObject text =
                root.has("text") && root.get("text").isJsonObject() ? root.getAsJsonObject("text")
                        : root;
        return new BeverageData.TextKeys(stringOrNull(text, "name_key"),
                stringOrNull(text, "lore_key"), stringOrNull(text, "tooltip_key"),
                stringOrNull(text, "effect_text_key"), stringOrNull(text, "brew_time_text_key"),
                stringOrNull(text, "ingredients_text_key"),
                stringOrNull(text, "container_text_key"), stringOrNull(text, "rarity_text_key"),
                stringOrNull(text, "category_text_key"), stringOrNull(text, "flavor_text_key"),
                stringOrNull(text, "warning_key"), stringOrNull(text, "crafting_instructions_key"));
    }

    private static BeverageData.Config parseConfig(JsonObject root) {
        JsonObject cfg = root.has("config") && root.get("config").isJsonObject()
                ? root.getAsJsonObject("config")
                : new JsonObject();
        return new BeverageData.Config(bool(cfg, "enabled", true),
                stringOrNull(cfg, "override_rarity"),
                cfg.has("override_stack_size") ? integer(cfg, "override_stack_size", 0) : null,
                cfg.has("override_loot_weight") ? integer(cfg, "override_loot_weight", 0) : null,
                bool(cfg, "disable_random_failures", false), bool(cfg, "disable_spoilage", false));
    }

    private static void loadContainers(Path containersPath, Map<Identifier, ContainerData> out)
            throws IOException {
        if (!Files.isDirectory(containersPath)) {
            return;
        }
        try (var paths = Files.walk(containersPath)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (Reader reader =
                        new BufferedReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                    @SuppressWarnings("null")
                    JsonObject root = GSON.fromJson(reader, JsonObject.class);
                    ContainerData data = parseContainer(root);
                    out.put(data.id(), data);
                } catch (Exception e) {
                    Alchemy.LOGGER.error("Failed to parse container JSON {}", path, e);
                }
            });
        }
    }

    private static ContainerData parseContainer(JsonObject root) {
        expectType(root, "alchemy:container");

        Identifier id = id(root, "id");
        String kind = string(root, "container_kind", "can");
        int stack = integer(root, "stack_size", 16);
        String rarity = string(root, "rarity", "common");

        JsonObject durabilityObj = root.has("durability") && root.get("durability").isJsonObject()
                ? root.getAsJsonObject("durability")
                : new JsonObject();
        ContainerData.Durability durability = new ContainerData.Durability(
                bool(durabilityObj, "breakable", true), integer(durabilityObj, "max_damage", 0),
                bool(durabilityObj, "fireproof", false),
                string(durabilityObj, "explosion_resistance", "low"));

        JsonObject interactionObj =
                root.has("interaction") && root.get("interaction").isJsonObject()
                        ? root.getAsJsonObject("interaction")
                        : new JsonObject();
        UseAction action = useAction(string(interactionObj, "use_action", "drink"));
        Identifier returnId =
                interactionObj.has("return_item_id") ? id(interactionObj, "return_item_id") : id; // default
                                                                                                  // to
                                                                                                  // itself
        ContainerData.Interaction interaction = new ContainerData.Interaction(action,
                bool(interactionObj, "returns_container", true), returnId,
                bool(interactionObj, "consume_on_use", false),
                bool(interactionObj, "consume_on_drink", true));

        JsonObject sealObj =
                root.has("seal") && root.get("seal").isJsonObject() ? root.getAsJsonObject("seal")
                        : new JsonObject();
        ContainerData.Seal seal = new ContainerData.Seal(bool(sealObj, "starts_sealed", true),
                bool(sealObj, "reopenable", true), string(sealObj, "seal_quality", "good"));

        ContainerData.StateStorage stateStorage = parseContainerStateStorage(root, id);

        return new ContainerData(id, kind, stack, rarity, durability, interaction, seal,
                stateStorage);
    }

    private static ContainerData.StateStorage parseContainerStateStorage(JsonObject root,
            Identifier id) {
        JsonObject stateStorageObj = object(root, "state_storage");
        JsonObject placedBlockObj = object(stateStorageObj, "placed_block");

        boolean enabled = bool(placedBlockObj, "enabled", placedBlockObj.has("block_id"));
        Identifier blockId = placedBlockObj.has("block_id") ? id(placedBlockObj, "block_id")
                : Identifier.of(id.getNamespace(), id.getPath() + "_block");
        Identifier blockEntityId =
                placedBlockObj.has("block_entity_id") ? id(placedBlockObj, "block_entity_id")
                        : blockId;
        boolean syncToClient = bool(placedBlockObj, "sync_to_client", true);
        boolean dropsKeepContents = bool(placedBlockObj, "drops_keep_contents", true);

        return new ContainerData.StateStorage(new ContainerData.PlacedBlock(enabled, blockId,
                blockEntityId, syncToClient, dropsKeepContents));
    }

    private static void loadEquipment(Path equipmentPath, Map<Identifier, EquipmentData> out)
            throws IOException {
        if (!Files.isDirectory(equipmentPath)) {
            return;
        }
        try (var paths = Files.walk(equipmentPath)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (Reader reader =
                        new BufferedReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                    @SuppressWarnings("null")
                    JsonObject root = GSON.fromJson(reader, JsonObject.class);
                    EquipmentData data = parseEquipment(root);
                    out.put(data.id(), data);
                } catch (Exception e) {
                    Alchemy.LOGGER.error("Failed to parse equipment JSON {}", path, e);
                }
            });
        }
    }

    private static EquipmentData parseEquipment(JsonObject root) {
        expectType(root, "alchemy:equipment");

        Identifier id = id(root, "id");
        String nameKey = string(root, "name_key", "");
        String rarity = string(root, "rarity", "common");
        String material = string(root, "material", "");
        String function = string(root, "function", "");
        int stack = integer(root, "stack_size", 1);

        EquipmentData.Placement placement = parseEquipmentPlacement(root, id);

        return new EquipmentData(id, nameKey, rarity, material, function, stack, placement);
    }

    private static EquipmentData.Placement parseEquipmentPlacement(JsonObject root, Identifier id) {
        JsonObject placementObj = object(root, "placement");
        String kind = string(placementObj, "kind", "");
        boolean blockEnabled = "block".equalsIgnoreCase(kind);
        Identifier blockId = placementObj.has("block_id") ? id(placementObj, "block_id")
                : Identifier.of(id.getNamespace(), id.getPath() + "_block");
        Identifier blockEntityId =
                placementObj.has("block_entity_id") ? id(placementObj, "block_entity_id") : blockId;
        return new EquipmentData.Placement(blockEnabled, blockId, blockEntityId);
    }

    // --- helpers ---------------------------------------------------------

    private static void expectType(JsonObject root, String expected) {
        String type = string(root, "type", "");
        if (!expected.equals(type)) {
            throw new IllegalArgumentException("Expected type " + expected + " but found " + type);
        }
    }

    private static JsonObject object(JsonObject root, String key) {
        return root.has(key) && root.get(key).isJsonObject() ? root.getAsJsonObject(key)
                : new JsonObject();
    }

    private static JsonArray array(JsonObject root, String key) {
        return root.has(key) && root.get(key).isJsonArray() ? root.getAsJsonArray(key)
                : new JsonArray();
    }

    private static Identifier id(JsonObject obj, String key) {
        return Identifier.of(string(obj, key, ""));
    }

    private static String string(JsonObject obj, String key, String def) {
        return obj.has(key) ? obj.get(key).getAsString() : def;
    }

    private static String stringOrNull(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    private static int integer(JsonObject obj, String key, int def) {
        return obj.has(key) ? obj.get(key).getAsInt() : def;
    }

    private static double number(JsonObject obj, String key, double def) {
        return obj.has(key) ? obj.get(key).getAsDouble() : def;
    }

    private static boolean bool(JsonObject obj, String key, boolean def) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : def;
    }

    private static UseAction useAction(String id) {
        return switch (id.toLowerCase()) {
            case "eat" -> UseAction.EAT;
            case "block" -> UseAction.BLOCK;
            case "bow" -> UseAction.BOW;
            case "crossbow" -> UseAction.CROSSBOW;
            case "spyglass" -> UseAction.SPYGLASS;
            default -> UseAction.DRINK;
        };
    }
}
