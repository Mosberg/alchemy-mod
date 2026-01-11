package dk.mosberg.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import dk.mosberg.registry.ModBlocks;

/**
 * Lightweight container block entity that just persists a payload NBT blob for future use.
 */
public class ContainerBlockEntity extends BlockEntity {
    private NbtCompound payload = new NbtCompound();

    public ContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.containerBlockEntityType(state.getBlock()), pos, state);
    }

    public NbtCompound payload() {
        return payload;
    }

    public void setPayload(NbtCompound payload) {
        this.payload = payload == null ? new NbtCompound() : payload.copy();
        markDirty();
    }
}
