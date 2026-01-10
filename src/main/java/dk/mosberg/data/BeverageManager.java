package dk.mosberg.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dk.mosberg.Alchemy;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Central registry of beverage definitions used by item registration.
 */
public final class BeverageManager {
    private static final Map<Identifier, BeverageData> BEVERAGES = new LinkedHashMap<>();

    static {
        register("coppercap_lager", StatusEffects.HASTE, minutes(5), 0, 4, 0.6F);
        register("frostmarsh_pils", StatusEffects.SPEED, minutes(3), 0, 4, 0.6F);
        register("emberhold_amber_ale", StatusEffects.FIRE_RESISTANCE, minutes(2), 0, 4, 0.6F);
        register("sunvale_golden_pale", StatusEffects.HASTE, minutes(2), 0, 4, 0.6F);
        register("stormwake_session_ipa", StatusEffects.DOLPHINS_GRACE, minutes(3), 0, 4, 0.6F);
        register("blackvault_stout", StatusEffects.RESISTANCE, minutes(3), 0, 5, 0.7F);
        register("thornveil_herbal_ale", StatusEffects.REGENERATION, seconds(45), 0, 4, 0.6F);
        register("ropesend_dockside_brew", StatusEffects.LUCK, minutes(2), 0, 4, 0.6F);
    }

    private BeverageManager() {}

    public static BeverageData register(String path,
            RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect, int durationTicks,
            int amplifier, int hunger, float saturation) {
        Identifier id = Identifier.of(Alchemy.MOD_ID, path);
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

    private static int minutes(int minutes) {
        return minutes * 20 * 60;
    }

    private static int seconds(int seconds) {
        return seconds * 20;
    }
}
