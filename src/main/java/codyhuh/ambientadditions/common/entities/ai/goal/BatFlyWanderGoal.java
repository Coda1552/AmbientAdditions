package codyhuh.ambientadditions.common.entities.ai.goal;

import codyhuh.ambientadditions.common.entities.WhiteFruitBat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BatFlyWanderGoal extends WaterAvoidingRandomStrollGoal {
    private final WhiteFruitBat mob;

    public BatFlyWanderGoal(WhiteFruitBat goalOwner, double speedMod, float probability) {
        super(goalOwner, speedMod, probability);
        this.mob = goalOwner;
    }

    @Override
    public void start() {
        super.start();

        if (!mob.isFlying() && mob.wantsToFly()) {
            mob.setDeltaMovement(mob.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
            mob.setFlying(true);
        }
    }

    @Override
    public boolean canUse() {
        return (forceTrigger || mob.wantsToFly()) && super.canUse();
    }

    @Override
    public void tick() {
        if (mob.wantsToFly() && !mob.onGround() && mob.getNavigation().isDone()) {
            trigger();
        }

        speedModifier = mob.isFlying() ? 10.0D : 1.0D;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec3 = this.mob.getViewVector(1.0F);
        int i = 32;
        int y = 16;
        Vec3 pos = findPos(this.mob, i, 4 * y, vec3.x, vec3.z, ((float)Math.PI / 2F), 8 * y, 6 * y);
        return pos != null ? pos : AirAndWaterRandomPos.getPos(this.mob, i, 4, -2, vec3.x, vec3.z, ((float)Math.PI / 2F));
    }

    @Nullable
    public static Vec3 findPos(PathfinderMob pMob, int pRadius, int pYRange, double pX, double pZ, float pAmplifier, int pMaxSwimUp, int pMinSwimUp) {
        boolean flag = GoalUtils.mobRestricted(pMob, pRadius);
        return RandomPos.generateRandomPos(pMob, () -> {
            BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(pMob.getRandom(), pRadius, pYRange, 24, pX, pZ, pAmplifier);
            if (blockpos == null) {
                return null;
            } else {
                BlockPos blockpos1 = LandRandomPos.generateRandomPosTowardDirection(pMob, pRadius, flag, blockpos);
                if (blockpos1 == null) {
                    return null;
                } else {
                    blockpos1 = RandomPos.moveUpToAboveSolid(blockpos1, pMob.getRandom().nextInt(pMaxSwimUp - pMinSwimUp + 1) + pMinSwimUp, pMob.level().getMaxBuildHeight(), (p_148486_) -> GoalUtils.isSolid(pMob, p_148486_));
                    return !GoalUtils.isWater(pMob, blockpos1) && !GoalUtils.hasMalus(pMob, blockpos1) ? blockpos1 : null;
                }
            }
        });
    }

    private boolean wantsToLand() {
        return mob.isFlying() && mob.getRandom().nextFloat() > 0.85F;
    }

    @Override
    public void stop() {
        if (wantsToLand()) {
            mob.landGoal.trigger();
        }
    }
}
