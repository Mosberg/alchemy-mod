package dk.mosberg.data;

import java.util.List;
import java.util.Objects;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/**
 * Immutable beverage definition assembled from JSON. Captures the essential gameplay-facing fields
 * needed to register drinkable items, compute food stats, and apply multiple effects with
 * per-effect chance.
 */
public record BeverageData(Identifier id, String category, String style, Identifier container,
        String rarity, int stackSize, Stats stats, List<EffectEntry> effects, TextKeys textKeys,
        Config config) {

    public BeverageData {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(container, "container");
        Objects.requireNonNull(stats, "stats");
        Objects.requireNonNull(effects, "effects");
    }

    /** Convenience accessor for the primary (first) effect. */
    public EffectEntry primaryEffect() {
        return effects.isEmpty() ? null : effects.getFirst();
    }

    /**
     * Builds a food component from the nutrition data. Effects are applied manually so they are not
     * duplicated here.
     */
    public FoodComponent foodComponent() {
        return new FoodComponent.Builder().nutrition(stats.nutrition().hunger())
                .saturationModifier(stats.nutrition().saturation()).alwaysEdible().build();
    }

    /**
     * Applies all configured effects to the consumer, honoring per-effect chance.
     */
    public void applyEffects(BeverageEffectSink sink, Random random) {
        for (EffectEntry entry : effects) {
            if (random.nextFloat() <= entry.chance()) {
                sink.accept(entry.toInstance());
            }
        }
    }

    /** Translation key helper with optional suffix. */
    public String translationKey(String suffix) {
        return "item." + id.getNamespace() + "." + id.getPath()
                + (suffix == null || suffix.isBlank() ? "" : "." + suffix);
    }

    // --- Nested records --------------------------------------------------

    public record Stats(double alcoholByVolume, double strength, Intoxication intoxication,
            Nutrition nutrition) {
    }

    public record Intoxication(double value, double decayRatePerTick) {
    }

    public record Nutrition(int hunger, float saturation) {
    }

    public record EffectEntry(RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect,
            int durationTicks, int amplifier, float chance, boolean showParticles, boolean showIcon,
            boolean ambient) {

        public StatusEffectInstance toInstance() {
            return new StatusEffectInstance(effect, durationTicks, amplifier, ambient,
                    showParticles, showIcon);
        }
    }

    public record TextKeys(String name, String lore, String tooltip, String effect, String brewTime,
            String ingredients, String container, String rarity, String category, String flavor,
            String warning, String crafting) {
    }

    public record Config(boolean enabled, String overrideRarity, Integer overrideStackSize,
            Integer overrideLootWeight, boolean disableRandomFailures, boolean disableSpoilage) {
    }

    /** Functional interface for applying computed effect instances. */
    @FunctionalInterface
    public interface BeverageEffectSink {
        void accept(StatusEffectInstance instance);
    }
}
