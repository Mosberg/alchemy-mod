package dk.mosberg.data;

import net.minecraft.util.Identifier;

/**
 * Immutable definition for equipment parsed from data/alchemy/equipment/*.json. This represents
 * only the fields we currently need to register placeholder items; the full JSON payload can be
 * extended onto this record later.
 */
public record EquipmentData(Identifier id, String nameKey, String rarity, String material,
        String function, int stackSize, Placement placement) {

    public record Placement(boolean blockEnabled, Identifier blockId, Identifier blockEntityId) {
    }
}
