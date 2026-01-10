package dk.mosberg.registry;

import dk.mosberg.Alchemy;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    public static final ItemGroup ALCHEMY_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Alchemy.MOD_ID, "main"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.alchemy.main"))
                    .icon(() -> new ItemStack(ModItems.COPPERCAP_LAGER))
                    .entries((context, entries) -> {
                        entries.add(ModItems.ALUMINUM_CAN);
                        ModItems.beverages().forEach(entries::add);
                    }).build());

    private ModItemGroups() {}

    public static void register() {
        // Trigger static initializers
    }
}
