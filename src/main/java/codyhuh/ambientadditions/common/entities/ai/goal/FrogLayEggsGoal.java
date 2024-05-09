package codyhuh.ambientadditions.common.entities.ai.goal;

import codyhuh.ambientadditions.common.entities.util.AbstractFrog;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

public class FrogLayEggsGoal extends MoveToBlockGoal {
    private final AbstractFrog frog;
    private final Block eggsBlock;

    public FrogLayEggsGoal(AbstractFrog frog, Block eggsBlock) {
        super(frog, 1.0D, 24, 4);
        this.frog = frog;
        this.eggsBlock = eggsBlock;
    }

    @Override
    public boolean canUse() {
        return frog.isGravid() && super.canUse();
    }

    @Override
    public double acceptedDistance() {
        return 1.5D;
    }

    @Override
    public void tick() {
        super.tick();

        if (!frog.isGravid()) {
            frog.setGravid(true);
        }

        if (isReachedTarget()) {
            frog.level().setBlock(getMoveToTarget(), eggsBlock.defaultBlockState(), 2);
            frog.playSound(SoundEvents.FROG_LAY_SPAWN);
            frog.setGravid(false);
            stop();
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.isWaterAt(pos);
    }

    @Override
    public void stop() {
        blockPos = BlockPos.ZERO;
    }
}
