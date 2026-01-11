package dk.mosberg.item;

import java.util.Objects;
import dk.mosberg.data.ContainerData;
import net.minecraft.item.Item;

/**
 * Simple placeholder item representing an empty container defined in JSON. Real container mechanics
 * (pressure, sealing, liquid handling) can be layered atop this item later; for now it preserves
 * stack size, rarity intent, and registry identity.
 */
public class ContainerItem extends Item {
    private final ContainerData data;

    public ContainerItem(ContainerData data) {
        super(new Item.Settings()
                .registryKey(net.minecraft.registry.RegistryKey
                        .of(net.minecraft.registry.RegistryKeys.ITEM, data.id()))
                .maxCount(data.stackSize()));
        this.data = Objects.requireNonNull(data, "data");
    }

    public ContainerData definition() {
        return data;
    }
}
