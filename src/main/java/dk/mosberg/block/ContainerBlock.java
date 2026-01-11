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
 * Basic container block driven by JSON metadata. Behavior is minimal for now and primarily exists
 * to back the placeholder items with a placeable block + block entity that can be extended later.
 */
public class ContainerBlock extends BlockWithEntity {
    public static final MapCodec<ContainerBlock> CODEC = createCodec(ContainerBlock::new);

    public ContainerBlock(Settings settings) {
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
    public ContainerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ContainerBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
            BlockHitResult hit) {
        // Future: open UI / fluid view. For now, succeed client-side for minimal feedback.
        return world.isClient() ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}
