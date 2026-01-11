package dk.mosberg.registry;

import java.util.Collection;
import dk.mosberg.Alchemy;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Registry for creative inventory item groups added by the Alchemy mod. Defines custom tabs that
 * organize mod items in the creative inventory.
 */
public final class ModItemGroups {
    /**
     * The main Alchemy creative tab containing all containers and beverages.
     */
    public static final ItemGroup ALCHEMY_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Alchemy.MOD_ID, "main"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.alchemy.main")).icon(
                    () -> new ItemStack(firstItem(ModItems.beverages(), ModItems.containers())))
                    .entries((context, entries) -> {
                        ModItems.containers().forEach(entries::add);
                        ModItems.beverages().forEach(entries::add);
                        ModItems.equipment().forEach(entries::add);
                    }).build());

    private ModItemGroups() {}

    /**
     * Triggers static initialization of all item groups. Called during mod initialization.
     */
    public static void register() {
        // Trigger static initializers
    }

    private static net.minecraft.item.Item firstItem(Collection<net.minecraft.item.Item> primary,
            Collection<net.minecraft.item.Item> fallback) {
        return primary.stream().findFirst()
                .orElseGet(() -> fallback.stream().findFirst().orElse(Items.GLASS_BOTTLE));
    }
}
