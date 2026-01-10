package dk.mosberg.data;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Immutable data definition for a canned beverage, containing all properties needed to create a
 * consumable beverage item with status effects and food properties.
 *
 * @param id the unique identifier for this beverage
 * @param effect the status effect registry entry to apply when consumed
 * @param durationTicks duration of the status effect in ticks (20 ticks = 1 second)
 * @param amplifier the amplifier level for the status effect (0 = level I)
 * @param hunger the food value/hunger points restored when consumed
 * @param saturation the saturation modifier for food properties
 */
public record BeverageData(Identifier id, RegistryEntry<StatusEffect> effect, int durationTicks,
        int amplifier, int hunger, float saturation) {

    /**
     * Creates a new status effect instance based on this beverage's effect configuration.
     *
     * @return a status effect instance with configured duration and amplifier
     */
    public StatusEffectInstance effectInstance() {
        return new StatusEffectInstance(effect, durationTicks, amplifier);
    }

    /**
     * Builds a food component for this beverage with configured nutrition values.
     *
     * @return a food component that is always edible regardless of hunger
     */
    public FoodComponent foodComponent() {
        return new FoodComponent.Builder().nutrition(hunger).saturationModifier(saturation)
                .alwaysEdible().build();
    }

    /**
     * Generates a translation key for localization strings related to this beverage.
     *
     * @param suffix the suffix to append (e.g., "desc", "effect", "tooltip"), or empty string for
     *        the base key
     * @return the full translation key in format "item.namespace.path.suffix"
     */
    public String translationKey(String suffix) {
        return "item." + id.getNamespace() + "." + id.getPath()
                + (suffix.isBlank() ? "" : "." + suffix);
    }
}
