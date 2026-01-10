package dk.mosberg.item;

import java.util.function.Consumer;
import dk.mosberg.data.BeverageData;
import dk.mosberg.effect.BeverageEffectManager;
import dk.mosberg.registry.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A consumable beverage item that grants status effects when drunk. Returns an empty aluminum can
 * after consumption.
 */
public class BeverageCanItem extends Item {
    private final BeverageData data;

    /**
     * Creates a new beverage can item with the specified beverage data.
     *
     * @param data the beverage definition containing effects and properties
     */
    public BeverageCanItem(BeverageData data) {
        super(new Item.Settings()
                .registryKey(net.minecraft.registry.RegistryKey
                        .of(net.minecraft.registry.RegistryKeys.ITEM, data.id()))
                .maxCount(16).food(BeverageEffectManager.toFoodComponent(data)));
        this.data = data;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient()) {
            user.addStatusEffect(data.effectInstance());

            // Return empty aluminum can to player inventory
            if (user instanceof PlayerEntity player) {
                if (!player.isCreative()) {
                    ItemStack container = new ItemStack(ModItems.ALUMINUM_CAN);
                    if (stack.isEmpty()) {
                        return container;
                    }
                    if (!player.getInventory().insertStack(container)) {
                        player.dropItem(container, false);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
            TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer,
            TooltipType type) {
        textConsumer
                .accept(Text.translatable(data.translationKey("desc")).formatted(Formatting.GRAY));
        textConsumer.accept(
                Text.translatable(data.translationKey("effect")).formatted(Formatting.GOLD));
        textConsumer.accept(Text.translatable(data.translationKey("ingredients"))
                .formatted(Formatting.DARK_GREEN));
        textConsumer.accept(
                Text.translatable(data.translationKey("brew_time")).formatted(Formatting.BLUE));
        textConsumer.accept(
                Text.translatable(data.translationKey("warning")).formatted(Formatting.DARK_RED));
    }

    /**
     * Returns the beverage data associated with this can.
     *
     * @return the beverage data
     */
    public BeverageData getData() {
        return data;
    }
}
