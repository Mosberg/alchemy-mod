package dk.mosberg.item;

import java.util.Objects;
import dk.mosberg.data.EquipmentData;
import net.minecraft.item.Item;

/**
 * Placeholder item for equipment definitions. Real block + block-entity implementations can swap
 * this out later; for now it ensures every equipment JSON materializes as a registrable item.
 */
public class EquipmentItem extends Item {
    private final EquipmentData data;

    public EquipmentItem(EquipmentData data) {
        super(new Item.Settings()
                .registryKey(net.minecraft.registry.RegistryKey
                        .of(net.minecraft.registry.RegistryKeys.ITEM, data.id()))
                .maxCount(data.stackSize()));
        this.data = Objects.requireNonNull(data, "data");
    }

    public EquipmentData definition() {
        return data;
    }
}
