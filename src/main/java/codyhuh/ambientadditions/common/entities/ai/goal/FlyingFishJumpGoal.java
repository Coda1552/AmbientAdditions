package codyhuh.ambientadditions.common.entities.ai.goal;

import codyhuh.ambientadditions.common.entities.FlyingFish;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class FlyingFishJumpGoal extends JumpGoal {
   private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
   private final FlyingFish fish;
   private final int interval;
   private boolean breached;

   public FlyingFishJumpGoal(FlyingFish p_i50329_1_, int p_i50329_2_) {
      this.fish = p_i50329_1_;
      this.interval = p_i50329_2_;
   }

   public boolean canUse() {
      if (this.fish.getRandom().nextInt(this.interval) != 0) {
         return false;
      } else {
         Direction direction = this.fish.getMotionDirection();
         int i = direction.getStepX();
         int j = direction.getStepZ();
         BlockPos blockpos = this.fish.blockPosition();

         for(int k : STEPS_TO_CHECK) {
            if (!this.waterIsClear(blockpos, i, j, k) || !this.surfaceIsClear(blockpos, i, j, k)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean waterIsClear(BlockPos p_220709_1_, int p_220709_2_, int p_220709_3_, int p_220709_4_) {
      BlockPos blockpos = p_220709_1_.offset(p_220709_2_ * p_220709_4_, 0, p_220709_3_ * p_220709_4_);
      return this.fish.level().getFluidState(blockpos).is(FluidTags.WATER) && !this.fish.level().getBlockState(blockpos).blocksMotion();
   }

   private boolean surfaceIsClear(BlockPos p_220708_1_, int p_220708_2_, int p_220708_3_, int p_220708_4_) {
      return this.fish.level().getBlockState(p_220708_1_.offset(p_220708_2_ * p_220708_4_, 1, p_220708_3_ * p_220708_4_)).isAir() && this.fish.level().getBlockState(p_220708_1_.offset(p_220708_2_ * p_220708_4_, 2, p_220708_3_ * p_220708_4_)).isAir();
   }

   public boolean canContinueToUse() {
      double d0 = this.fish.getDeltaMovement().y;
      return (!(d0 * d0 < (double)0.03F) || this.fish.getXRot() == 0.0F || !(Math.abs(this.fish.getXRot()) < 10.0F) || !this.fish.isInWater()) && !this.fish.onGround();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      Direction direction = this.fish.getMotionDirection();
      this.fish.setDeltaMovement(this.fish.getDeltaMovement().add((double)direction.getStepX() * 2D, 1.0D, (double)direction.getStepZ() * 2D));
      this.fish.setDeltaMovement(this.fish.getDeltaMovement().multiply(2, 1, 2));
      this.fish.getNavigation().stop();
      this.fish.setFlying(true);
   }

   public void stop() {
      this.fish.setXRot(0.0F);
      this.fish.setFlying(false);
   }

   public void tick() {
      boolean flag = this.breached;
      if (!flag) {
         FluidState fluidstate = this.fish.level().getFluidState(this.fish.blockPosition());
         this.breached = fluidstate.is(FluidTags.WATER);
      }

      if (this.breached && !flag) {
         this.fish.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      Vec3 vector3d = this.fish.getDeltaMovement();
      if (vector3d.y * vector3d.y < (double)0.03F && this.fish.getXRot() != 0.0F) {
         this.fish.setXRot(Mth.rotLerp(this.fish.getXRot(), 0.0F, 0.2F));
      } else {
         double d0 = vector3d.horizontalDistanceSqr();
         double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double)(180F / (float)Math.PI);
         this.fish.setXRot((float) d1);
      }

   }
}
