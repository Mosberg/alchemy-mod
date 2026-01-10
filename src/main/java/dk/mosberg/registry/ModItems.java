package dk.mosberg.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dk.mosberg.Alchemy;
import dk.mosberg.data.BeverageData;
import dk.mosberg.data.BeverageManager;
import dk.mosberg.item.BeverageCanItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    private static final Map<Identifier, BeverageCanItem> BEVERAGE_ITEMS = new LinkedHashMap<>();

    public static final Item ALUMINUM_CAN = register("aluminum_can",
            new Item(new Item.Settings().registryKey(
                    net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.ITEM,
                            Identifier.of(Alchemy.MOD_ID, "aluminum_can")))
                    .maxCount(16)));

    public static final Item ALUMINUM_KEG = register("aluminum_keg",
            new Item(new Item.Settings().registryKey(
                    net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.ITEM,
                            Identifier.of(Alchemy.MOD_ID, "aluminum_keg")))
                    .maxCount(1)));

    public static final BeverageCanItem COPPERCAP_LAGER = registerBeverage("coppercap_lager");
    public static final BeverageCanItem FROSTMARSH_PILS = registerBeverage("frostmarsh_pils");
    public static final BeverageCanItem EMBERHOLD_AMBER_ALE =
            registerBeverage("emberhold_amber_ale");
    public static final BeverageCanItem SUNVALE_GOLDEN_PALE =
            registerBeverage("sunvale_golden_pale");
    public static final BeverageCanItem STORMWAKE_SESSION_IPA =
            registerBeverage("stormwake_session_ipa");
    public static final BeverageCanItem BLACKVAULT_STOUT = registerBeverage("blackvault_stout");
    public static final BeverageCanItem THORNVEIL_HERBAL_ALE =
            registerBeverage("thornveil_herbal_ale");
    public static final BeverageCanItem ROPESEND_DOCKSIDE_BREW =
            registerBeverage("ropesend_dockside_brew");

    // Spirits
    public static final BeverageCanItem CHORUS_BLOOM_GIN = registerBeverage("chorus_bloom_gin");
    public static final BeverageCanItem DUNE_MIRAGE_RUM = registerBeverage("dune_mirage_rum");
    public static final BeverageCanItem FROSTPETAL_SCHNAPPS =
            registerBeverage("frostpetal_schnapps");
    public static final BeverageCanItem HOLLOWSHADE_ABSINTHE =
            registerBeverage("hollowshade_absinthe");
    public static final BeverageCanItem SCARABGOLD_BRANDY = registerBeverage("scarabgold_brandy");
    public static final BeverageCanItem SOULFLAME_SPIRIT = registerBeverage("soulflame_spirit");

    private ModItems() {}

    public static void register() {
        // Intentionally left blank; class loading triggers static initializers.
    }

    public static Collection<BeverageCanItem> beverages() {
        return BEVERAGE_ITEMS.values();
    }

    private static BeverageCanItem registerBeverage(String path) {
        BeverageData data =
                Objects.requireNonNull(BeverageManager.get(Identifier.of(Alchemy.MOD_ID, path)),
                        () -> "Missing beverage definition for " + path);
        BeverageCanItem item = new BeverageCanItem(data);
        register(path, item);
        BEVERAGE_ITEMS.put(data.id(), item);
        return item;
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(Alchemy.MOD_ID, name), item);
    }
}
