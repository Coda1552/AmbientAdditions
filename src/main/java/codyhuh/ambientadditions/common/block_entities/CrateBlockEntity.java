package codyhuh.ambientadditions.common.block_entities;

import codyhuh.ambientadditions.registry.AABlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrateBlockEntity extends BlockEntity {
    public static final String DATA = "CreatureData";

    public CrateBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AABlockEntities.CRATE.get(), p_155229_, p_155230_);
    }

    public void setCreatureData(CompoundTag creatureTag, CompoundTag beTag) {
        beTag.put(DATA, creatureTag);
    }

    public CompoundTag getCreatureData() {
        return getPersistentData().getCompound(DATA);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        //tag.put(DATA, getCreatureData(tag));
        super.saveAdditional(tag);
    }
}
