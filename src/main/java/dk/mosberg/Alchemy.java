package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.data.ContentPack;
import dk.mosberg.data.DataLoader;
import dk.mosberg.registry.ModEffects;
import dk.mosberg.registry.ModItemGroups;
import dk.mosberg.registry.ModItems;
import net.fabricmc.api.ModInitializer;

/**
 * Main entry point for the Alchemy mod. Handles initialization of data-driven beverage definitions,
 * item registration, and mod setup.
 */
public class Alchemy implements ModInitializer {
    /** The mod identifier used for namespacing registry entries and resources. */
    public static final String MOD_ID = "alchemy";

    /** Logger instance for mod-wide logging. */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Load data-driven definitions first before item registration
        ContentPack content = DataLoader.loadAll();

        // Register game content
        ModEffects.register();
        ModItems.register(content);
        ModItemGroups.register();

        LOGGER.info("{} initialized successfully.", MOD_ID);
    }
}
