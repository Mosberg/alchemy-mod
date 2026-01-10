package dk.mosberg.registry;

import dk.mosberg.Alchemy;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.alchemy.main"))
                    .icon(() -> new ItemStack(ModItems.COPPERCAP_LAGER))
                    .entries((context, entries) -> {
                        // Add containers first
                        entries.add(ModItems.ALUMINUM_CAN);
                        entries.add(ModItems.ALUMINUM_KEG);
                        // Add all registered beverages
                        ModItems.beverages().forEach(entries::add);
                    }).build());

    private ModItemGroups() {}

    /**
     * Triggers static initialization of all item groups. Called during mod initialization.
     */
    public static void register() {
        // Trigger static initializers
    }
}
