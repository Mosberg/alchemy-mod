package dk.mosberg.block;

import dk.mosberg.data.ContainerData;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * BlockItem wrapper for container blocks to honor stack size defined in JSON.
 */
public class ContainerBlockItem extends BlockItem {
    public ContainerBlockItem(Block block, ContainerData data) {
        super(block, new Item.Settings().maxCount(Math.max(1, data.stackSize())));
    }
}
