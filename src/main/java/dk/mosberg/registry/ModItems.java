package dk.mosberg.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dk.mosberg.data.BeverageData;
import dk.mosberg.data.BeverageManager;
import dk.mosberg.data.ContainerData;
import dk.mosberg.data.ContentPack;
import dk.mosberg.data.EquipmentData;
import dk.mosberg.item.BeverageCanItem;
import dk.mosberg.item.BeverageKegItem;
import dk.mosberg.item.ContainerItem;
import dk.mosberg.item.EquipmentItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Runtime item registration driven entirely by JSON definitions.
 */
public final class ModItems {
        private static final Map<Identifier, Item> CONTAINER_ITEMS = new LinkedHashMap<>();
        private static final Map<Identifier, Item> BEVERAGE_ITEMS = new LinkedHashMap<>();
        private static final Map<Identifier, Item> EQUIPMENT_ITEMS = new LinkedHashMap<>();

        private ModItems() {}

        public static void register(ContentPack content) {
                Objects.requireNonNull(content, "content");
                registerContainers(content);
                registerBeverages(content);
                registerEquipment(content);
        }

        public static Collection<Item> beverages() {
                return BEVERAGE_ITEMS.values();
        }

        public static Collection<Item> containers() {
                return CONTAINER_ITEMS.values();
        }

        public static Collection<Item> equipment() {
                return EQUIPMENT_ITEMS.values();
        }

        private static void registerContainers(ContentPack content) {
                for (ContainerData data : content.containerValues()) {
                        ContainerItem item = new ContainerItem(data);
                        register(data.id(), item);
                        CONTAINER_ITEMS.put(data.id(), item);
                }
        }

        private static void registerBeverages(ContentPack content) {
                // Ensure beverage definitions are known to the manager for later lookups
                BeverageManager.registerAll(content.beverageValues());

                for (BeverageData data : content.beverageValues()) {
                        ContainerData containerDef = content.container(data.container());
                        Item returnItem = resolveReturnItem(containerDef);
                        Item beverageItem = isKeg(containerDef)
                                        ? new BeverageKegItem(data, containerDef, returnItem)
                                        : new BeverageCanItem(data, containerDef, returnItem);

                        register(data.id(), beverageItem);
                        BEVERAGE_ITEMS.put(data.id(), beverageItem);
                }
        }

        private static void registerEquipment(ContentPack content) {
                for (EquipmentData data : content.equipmentValues()) {
                        EquipmentItem item = new EquipmentItem(data);
                        register(data.id(), item);
                        EQUIPMENT_ITEMS.put(data.id(), item);
                }
        }

        private static boolean isKeg(ContainerData containerDef) {
                return containerDef != null && "keg".equalsIgnoreCase(containerDef.containerKind());
        }

        private static Item resolveReturnItem(ContainerData containerDef) {
                if (containerDef == null || containerDef.interaction() == null) {
                        return null;
                }
                Identifier returnId = containerDef.interaction().returnItemId();
                return CONTAINER_ITEMS.getOrDefault(returnId, null);
        }

        private static <T extends Item> T register(Identifier id, T item) {
                return Registry.register(Registries.ITEM, id, item);
        }
}
