package codyhuh.ambientadditions.common.entities.ai.goal;

import codyhuh.ambientadditions.AmbientAdditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class MoleDiggingGoal extends Goal {
   private static final ResourceLocation DIGGING_LOOT = new ResourceLocation(AmbientAdditions.MOD_ID, "gameplay/mole_digging");
   private final Mob mole;
   private final Level level;
   private int eatAnimationTick;

   public MoleDiggingGoal(Mob p_i45314_1_) {
      this.mole = p_i45314_1_;
      this.level = p_i45314_1_.level();
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
   }

   @Override
   public boolean canUse() {
      if (this.mole.getRandom().nextInt(this.mole.isBaby() ? 500 : 1000) != 0) {
         return false;
      } else {
         BlockPos blockpos = this.mole.blockPosition();
         return this.level.getBlockState(blockpos.below()).is(BlockTags.DIRT);
      }
   }

   @Override
   public void start() {
      this.eatAnimationTick = 90;
      this.level.broadcastEntityEvent(this.mole, (byte)10);
      this.mole.getNavigation().stop();
   }

   @Override
   public void stop() {
      this.eatAnimationTick = 0;
   }

   @Override
   public boolean canContinueToUse() {
      return this.eatAnimationTick > 0;
   }

   public int getEatAnimationTick() {
      return this.eatAnimationTick;
   }

   @Override
   public void tick() {
      this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      BlockPos blockpos = this.mole.blockPosition();
      Vec3 pos = mole.position();

      if (this.eatAnimationTick == 79) {
         if (this.level.getBlockState(blockpos.below()).is(BlockTags.DIRT)) {

            this.level.playSound(null, blockpos, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS, 0.6F, 1.0F);
            this.mole.ate();

            for (int i = 0; i < 20; i++) {
               this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockpos.below())), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            }
         }
      }
      else if (this.eatAnimationTick == 55) {
         this.level.playSound(null, blockpos, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
      }
      else if (this.eatAnimationTick == 47 && (level.getBlockState(blockpos.below()).is(Blocks.FARMLAND)) || level.isRainingAt(blockpos)) {
         List<ItemStack> items = mole.level().getServer().getLootData().getLootTable(DIGGING_LOOT).getRandomItems(new LootParams.Builder((ServerLevel) mole.level()).create(LootContextParamSets.EMPTY));

         ItemEntity itemEntity = EntityType.ITEM.create(level);

         itemEntity.setItem(items.get(0));
         itemEntity.moveTo(pos);

         level.addFreshEntity(itemEntity);

         this.level.playSound(null, blockpos, SoundEvents.SLIME_JUMP, SoundSource.BLOCKS, 0.25F, 1.2F);
      }
   }
}
