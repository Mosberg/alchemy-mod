package dk.mosberg.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dk.mosberg.Alchemy;
import dk.mosberg.block.ContainerBlock;
import dk.mosberg.block.ContainerBlockEntity;
import dk.mosberg.block.ContainerBlockItem;
import dk.mosberg.block.EquipmentBlock;
import dk.mosberg.block.EquipmentBlockEntity;
import dk.mosberg.block.EquipmentBlockItem;
import dk.mosberg.data.ContainerData;
import dk.mosberg.data.ContentPack;
import dk.mosberg.data.EquipmentData;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registers data-driven blocks, block entities, and their block items for containers and equipment.
 * Behavior is intentionally minimal for now but establishes real placeable blocks.
 */
public final class ModBlocks {
    private static final Map<Identifier, Block> CONTAINER_BLOCKS = new LinkedHashMap<>();
    private static final Map<Identifier, Block> EQUIPMENT_BLOCKS = new LinkedHashMap<>();
    private static final Map<Block, BlockEntityType<ContainerBlockEntity>> CONTAINER_BE_TYPES =
            new LinkedHashMap<>();
    private static final Map<Block, BlockEntityType<EquipmentBlockEntity>> EQUIPMENT_BE_TYPES =
            new LinkedHashMap<>();
    private static final Map<Identifier, Item> BLOCK_ITEMS = new LinkedHashMap<>();

    private ModBlocks() {}

    public static void register(ContentPack content) {
        Objects.requireNonNull(content, "content");
        registerContainerBlocks(content);
        registerEquipmentBlocks(content);
    }

    public static Collection<Item> blockItems() {
        return BLOCK_ITEMS.values();
    }

    public static BlockEntityType<ContainerBlockEntity> containerBlockEntityType(Block block) {
        return CONTAINER_BE_TYPES.get(block);
    }

    public static BlockEntityType<EquipmentBlockEntity> equipmentBlockEntityType(Block block) {
        return EQUIPMENT_BE_TYPES.get(block);
    }

    private static void registerContainerBlocks(ContentPack content) {
        for (ContainerData data : content.containerValues()) {
            var placed = data.stateStorage() != null ? data.stateStorage().placedBlock() : null;
            if (placed == null || !placed.enabled()) {
                continue;
            }

            ContainerBlock block = new ContainerBlock(Block.Settings.create().strength(2.0f));
            registerBlock(placed.blockId(), block);
            BlockEntityType<ContainerBlockEntity> type =
                    FabricBlockEntityTypeBuilder.create(ContainerBlockEntity::new, block).build();
            Registry.register(Registries.BLOCK_ENTITY_TYPE, placed.blockEntityId(), type);
            CONTAINER_BE_TYPES.put(block, type);
            CONTAINER_BLOCKS.put(placed.blockId(), block);

            Item blockItem = new ContainerBlockItem(block, data);
            registerItem(placed.blockId(), blockItem);
        }
    }

    private static void registerEquipmentBlocks(ContentPack content) {
        for (EquipmentData data : content.equipmentValues()) {
            EquipmentData.Placement placement = data.placement();
            if (placement == null || !placement.blockEnabled()) {
                continue;
            }

            EquipmentBlock block = new EquipmentBlock(Block.Settings.create().strength(3.0f));
            registerBlock(placement.blockId(), block);
            BlockEntityType<EquipmentBlockEntity> type =
                    FabricBlockEntityTypeBuilder.create(EquipmentBlockEntity::new, block).build();
            Registry.register(Registries.BLOCK_ENTITY_TYPE, placement.blockEntityId(), type);
            EQUIPMENT_BE_TYPES.put(block, type);
            EQUIPMENT_BLOCKS.put(placement.blockId(), block);

            Item blockItem = new EquipmentBlockItem(block, data);
            registerItem(placement.blockId(), blockItem);
        }
    }

    private static <T extends Block> void registerBlock(Identifier id, T block) {
        Registry.register(Registries.BLOCK, id, block);
        Alchemy.LOGGER.info("Registered block {}", id);
    }

    private static <T extends Item> void registerItem(Identifier id, T item) {
        Registry.register(Registries.ITEM, id, item);
        BLOCK_ITEMS.put(id, item);
    }
}
