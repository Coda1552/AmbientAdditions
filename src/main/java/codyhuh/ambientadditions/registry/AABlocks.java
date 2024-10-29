package codyhuh.ambientadditions.registry;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.blocks.AAFrogspawnBlock;
import codyhuh.ambientadditions.common.blocks.CrateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AABlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AmbientAdditions.MOD_ID);

    public static final RegistryObject<Block> LEAF_FROGSPAWN = BLOCKS.register("leaf_frogspawn", () -> new AAFrogspawnBlock(AAEntities.LEAF_FROG_TADPOLE::get, BlockBehaviour.Properties.of().instabreak().noOcclusion().noCollission().sound(SoundType.FROGSPAWN)));
    public static final RegistryObject<Block> CRATE = BLOCKS.register("crate", () -> new CrateBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_PLANKS)));
}
