package dk.mosberg.data;

import java.util.Collection;
import java.util.Map;
import net.minecraft.util.Identifier;

/**
 * Aggregates all parsed content definitions loaded from JSON during startup.
 */
public record ContentPack(Map<Identifier, BeverageData> beverages,
        Map<Identifier, ContainerData> containers, Map<Identifier, EquipmentData> equipment) {

    public Collection<BeverageData> beverageValues() {
        return beverages.values();
    }

    public Collection<ContainerData> containerValues() {
        return containers.values();
    }

    public Collection<EquipmentData> equipmentValues() {
        return equipment.values();
    }

    public ContainerData container(Identifier id) {
        return containers.get(id);
    }
}
