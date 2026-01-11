package dk.mosberg.block;

import dk.mosberg.data.EquipmentData;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * BlockItem for equipment blocks honoring stack size from JSON.
 */
public class EquipmentBlockItem extends BlockItem {
    public EquipmentBlockItem(Block block, EquipmentData data) {
        super(block, new Item.Settings().maxCount(Math.max(1, data.stackSize())));
    }
}
