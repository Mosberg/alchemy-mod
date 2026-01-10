package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;

public class Alchemy implements ModInitializer {
    public static final String MOD_ID = "alchemy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Alchemy initialized.");
    }
}
