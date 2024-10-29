package codyhuh.ambientadditions;

import codyhuh.ambientadditions.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AmbientAdditions.MOD_ID)
public class AmbientAdditions {
    public static final String MOD_ID = "ambientadditions";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public AmbientAdditions() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        AAItems.ITEMS.register(bus);
        AAEntities.ENTITIES.register(bus);
        AASounds.SOUNDS.register(bus);
        AABlocks.BLOCKS.register(bus);
        AABlockEntities.BLOCK_ENTITIES.register(bus);
        AAParticles.PARTICLES.register(bus);
        AACreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }

    public static int sedationLvlRequiredToCapture(float health) {
        if (health <= 5) {
            return 1;
        }
        else if (health <= 10) {
            return 2;
        }
        else if (health <= 20) {
            return 3;
        }
        else if (health <= 50) {
            return 4;
        }
        else {
            return 5;
        }
    }
}