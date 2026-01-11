package dk.mosberg.effect;

import dk.mosberg.data.BeverageData;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Utility helpers for translating beverage data into runtime components.
 */
public final class BeverageEffectManager {
    private BeverageEffectManager() {}

    public static FoodComponent toFoodComponent(BeverageData data) {
        return data.foodComponent();
    }

    public static StatusEffectInstance primaryEffect(BeverageData data) {
        var primary = data.primaryEffect();
        return primary == null ? null : primary.toInstance();
    }
}
