package dk.mosberg.effect;

import dk.mosberg.data.BeverageData;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Utility class for converting beverage definitions into Minecraft game components. Provides helper
 * methods to transform BeverageData into consumable effects and food properties.
 */
public final class BeverageEffectManager {
    private BeverageEffectManager() {}

    /**
     * Creates a status effect instance from beverage data.
     *
     * @param data the beverage definition
     * @return a configured status effect instance
     */
    public static StatusEffectInstance toStatusEffect(BeverageData data) {
        return data.effectInstance();
    }

    /**
     * Builds a food component from beverage data with configured nutrition values.
     *
     * @param data the beverage definition
     * @return a food component suitable for item registration
     */
    public static FoodComponent toFoodComponent(BeverageData data) {
        return data.foodComponent();
    }
}
