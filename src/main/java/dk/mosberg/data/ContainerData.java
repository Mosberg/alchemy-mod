package dk.mosberg.data;

import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Identifier;

/**
 * Immutable definition of a container parsed from data/alchemy/containers/*.json. Only the fields
 * required for item registration and basic interactions are represented here. Additional raw data
 * can be added later as needed.
 */
public record ContainerData(Identifier id, String containerKind, int stackSize, String rarity,
        Durability durability, Interaction interaction, Seal seal, StateStorage stateStorage) {

    public record Durability(boolean breakable, int maxDamage, boolean fireproof,
            String explosionResistance) {
    }

    public record Interaction(UseAction useAction, boolean returnsContainer,
            Identifier returnItemId, boolean consumeOnUse, boolean consumeOnDrink) {
    }

    public record Seal(boolean startsSealed, boolean reopenable, String sealQuality) {
    }

    public record StateStorage(PlacedBlock placedBlock) {
    }

    public record PlacedBlock(boolean enabled, Identifier blockId, Identifier blockEntityId,
            boolean syncToClient, boolean dropsKeepContents) {
    }
}
