package dk.mosberg.block;

import dk.mosberg.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Stub equipment block entity that persists a small NBT blob for later upgrade to full logic.
 */
public class EquipmentBlockEntity extends BlockEntity {
    private NbtCompound payload = new NbtCompound();

    public EquipmentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.equipmentBlockEntityType(state.getBlock()), pos, state);
    }

    public NbtCompound payload() {
        return payload;
    }

    public void setPayload(NbtCompound payload) {
        this.payload = payload == null ? new NbtCompound() : payload.copy();
        markDirty();
    }
}
