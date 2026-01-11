package dk.mosberg.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Minimal equipment block with a backing block entity; ready to be extended with processing logic.
 */
public class EquipmentBlock extends BlockWithEntity {
    public static final MapCodec<EquipmentBlock> CODEC = createCodec(EquipmentBlock::new);

    public EquipmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public EquipmentBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EquipmentBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
            BlockHitResult hit) {
        return world.isClient() ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}
