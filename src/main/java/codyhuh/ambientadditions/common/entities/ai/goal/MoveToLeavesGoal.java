package codyhuh.ambientadditions.common.entities.ai.goal;

import codyhuh.ambientadditions.common.entities.WhiteFruitBat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class MoveToLeavesGoal extends MoveToBlockGoal {
    private final WhiteFruitBat bat;

    public MoveToLeavesGoal(WhiteFruitBat mob) {
        super(mob, 3.0D, 8, 24);
        this.bat = mob;
    }

    @Override
    public boolean canUse() {
        return bat.level().isDay() && super.canUse() && !bat.isResting();
    }

    @Override
    public double acceptedDistance() {
        return 0.1D;
    }

    @Override
    public void tick() {
        super.tick();

        if (isReachedTarget()) {
            bat.playSound(SoundEvents.AZALEA_LEAVES_PLACE);
            bat.setResting(true);
            stop();
        }

    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(BlockTags.LEAVES);
    }

    @Override
    public void stop() {
        blockPos = BlockPos.ZERO;
    }
}
