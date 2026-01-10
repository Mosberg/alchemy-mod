package dk.mosberg.effect;

import dk.mosberg.data.BeverageData;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Small helpers for turning beverage definitions into consumable effects.
 */
public final class BeverageEffectManager {
    private BeverageEffectManager() {}

    public static StatusEffectInstance toStatusEffect(BeverageData data) {
        return data.effectInstance();
    }

    public static FoodComponent toFoodComponent(BeverageData data) {
        return data.foodComponent();
    }
}
