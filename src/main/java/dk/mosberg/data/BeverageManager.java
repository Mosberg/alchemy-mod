package dk.mosberg.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.registry.entry.RegistryEntry;
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
     * Registers a new beverage definition with the given properties.
     *
     * @param id the unique identifier for this beverage
     * @param effect the status effect to apply when consumed
     * @param durationTicks duration of the effect in game ticks
     * @param amplifier the amplifier level (0-based)
     * @param hunger hunger points restored
     * @param saturation saturation modifier
     * @return the registered BeverageData instance
     * @throws NullPointerException if effect is null
     */
    public static BeverageData register(Identifier id,
            RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect, int durationTicks,
            int amplifier, int hunger, float saturation) {
        Objects.requireNonNull(id, "Beverage id cannot be null");
        Objects.requireNonNull(effect, "Status effect cannot be null");

        BeverageData data =
                new BeverageData(id, effect, durationTicks, amplifier, hunger, saturation);
        BEVERAGES.put(id, data);
        return data;
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
