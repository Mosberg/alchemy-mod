package dk.mosberg.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.Identifier;

/**
 * Central registry for beverage definitions loaded from data/alchemy/alcohol/*.json files. All
 * beverages must be registered before items are created. Thread-safe as long as all registration
 * happens during mod initialization.
 */
public final class BeverageManager {
    private static final Map<Identifier, BeverageData> BEVERAGES = new LinkedHashMap<>();

    private BeverageManager() {}

    /**
     * Registers a new beverage definition.
     */
    public static BeverageData register(BeverageData data) {
        Objects.requireNonNull(data, "Beverage data cannot be null");
        BEVERAGES.put(data.id(), data);
        return data;
    }

    /** Registers all beverages from the provided collection. */
    public static void registerAll(Collection<BeverageData> beverages) {
        beverages.forEach(BeverageManager::register);
    }

    /**
     * Retrieves a registered beverage definition by its identifier.
     *
     * @param id the beverage identifier
     * @return the beverage data, or null if not registered
     */
    public static BeverageData get(Identifier id) {
        return BEVERAGES.get(id);
    }

    /**
     * Returns an unmodifiable view of all registered beverages.
     *
     * @return collection of all registered beverage definitions
     */
    public static Collection<BeverageData> all() {
        return BEVERAGES.values();
    }
}
