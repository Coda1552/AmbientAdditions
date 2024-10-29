package codyhuh.ambientadditions.registry;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.block_entities.CrateBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AmbientAdditions.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrateBlockEntity>> CRATE = BLOCK_ENTITIES.register("crate", () -> BlockEntityType.Builder.of(CrateBlockEntity::new, AABlocks.CRATE.get()).build(null));
}