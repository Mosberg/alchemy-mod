package dk.mosberg.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Central registry of beverage definitions used by item registration. Beverages are loaded from
 * data/alchemy/alcohol/*.json files via DataLoader during mod initialization.
 */
public final class BeverageManager {
    private static final Map<Identifier, BeverageData> BEVERAGES = new LinkedHashMap<>();

    private BeverageManager() {}

    public static BeverageData register(Identifier id,
            RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect, int durationTicks,
            int amplifier, int hunger, float saturation) {
        BeverageData data = new BeverageData(id, Objects.requireNonNull(effect), durationTicks,
                amplifier, hunger, saturation);
        BEVERAGES.put(id, data);
        return data;
    }

    public static BeverageData get(Identifier id) {
        return BEVERAGES.get(id);
    }

    public static Collection<BeverageData> all() {
        return BEVERAGES.values();
    }
}
