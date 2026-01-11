package dk.mosberg.item;

import java.util.Objects;
import java.util.function.Consumer;
import dk.mosberg.data.BeverageData;
import dk.mosberg.data.ContainerData;
import dk.mosberg.effect.BeverageEffectManager;
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
 * Generic beverage item powered entirely by JSON definitions. Applies multiple effects with
 * per-effect chance and returns the configured empty container when consumed.
 */
public class BeverageCanItem extends Item {
    private final BeverageData data;
    private final ContainerData containerData;
    private final Item returnItem;

    public BeverageCanItem(BeverageData data, ContainerData containerData, Item returnItem) {
        super(new Item.Settings()
                .registryKey(net.minecraft.registry.RegistryKey
                        .of(net.minecraft.registry.RegistryKeys.ITEM, data.id()))
                .maxCount(data.stackSize()).food(BeverageEffectManager.toFoodComponent(data)));
        this.data = Objects.requireNonNull(data, "data");
        this.containerData = containerData;
        this.returnItem = returnItem;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (containerData != null && containerData.interaction() != null
                && containerData.interaction().useAction() != null) {
            return containerData.interaction().useAction();
        }
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
            data.applyEffects(user::addStatusEffect, world.getRandom());

            if (user instanceof PlayerEntity player && returnItem != null && !player.isCreative()) {
                ItemStack container = new ItemStack(returnItem);
                if (stack.isEmpty()) {
                    return container;
                }
                if (!player.getInventory().insertStack(container)) {
                    player.dropItem(container, false);
                }
            }
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
            TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer,
            TooltipType type) {
        var keys = data.textKeys();

        addText(textConsumer, keys != null ? keys.lore() : null, data.translationKey("lore"),
                Formatting.GRAY);
        addText(textConsumer, keys != null ? keys.effect() : null, data.translationKey("effects"),
                Formatting.GOLD);
        addText(textConsumer, keys != null ? keys.ingredients() : null,
                data.translationKey("ingredients"), Formatting.DARK_GREEN);
        addText(textConsumer, keys != null ? keys.brewTime() : null,
                data.translationKey("brew_time"), Formatting.BLUE);
        addText(textConsumer, keys != null ? keys.warning() : null, data.translationKey("warning"),
                Formatting.DARK_RED);
        addText(textConsumer, keys != null ? keys.flavor() : null,
                data.translationKey("flavor_text"), Formatting.ITALIC, Formatting.DARK_GRAY);
    }

    private static void addText(Consumer<Text> consumer, String explicitKey, String fallbackKey,
            Formatting... formatting) {
        String key = explicitKey != null && !explicitKey.isBlank() ? explicitKey : fallbackKey;
        consumer.accept(Text.translatable(key).formatted(formatting));
    }

    public BeverageData getData() {
        return data;
    }
}
