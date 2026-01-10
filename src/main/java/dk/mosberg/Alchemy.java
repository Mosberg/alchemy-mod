package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.registry.ModEffects;
import dk.mosberg.registry.ModItemGroups;
import dk.mosberg.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class Alchemy implements ModInitializer {
    public static final String MOD_ID = "alchemy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEffects.register();
        ModItems.register();
        ModItemGroups.register();

        LOGGER.info("Alchemy initialized.");
    }
}
