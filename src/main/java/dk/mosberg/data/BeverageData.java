package dk.mosberg.data;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Immutable definition for a canned beverage, including its status effect and food stats.
 */
public record BeverageData(Identifier id, RegistryEntry<StatusEffect> effect, int durationTicks,
        int amplifier, int hunger, float saturation) {

    public StatusEffectInstance effectInstance() {
        return new StatusEffectInstance(effect, durationTicks, amplifier);
    }

    public FoodComponent foodComponent() {
        return new FoodComponent.Builder().nutrition(hunger).saturationModifier(saturation)
                .alwaysEdible().build();
    }

    public String translationKey(String suffix) {
        return "item." + id.getNamespace() + "." + id.getPath()
                + (suffix.isBlank() ? "" : "." + suffix);
    }
}
