package codyhuh.ambientadditions.common.entities;

import codyhuh.ambientadditions.common.entities.util.AAAnimations;
import codyhuh.ambientadditions.registry.AAEntities;
import codyhuh.ambientadditions.registry.AAItems;
import codyhuh.ambientadditions.registry.AASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class Iiwi extends TamableAnimal implements FlyingAnimal, GeoEntity {
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   private float flapping = 1.0F;

   public Iiwi(EntityType<? extends Iiwi> p_i50251_1_, Level p_i50251_2_) {
      super(p_i50251_1_, p_i50251_2_);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_213386_1_, DifficultyInstance p_213386_2_, MobSpawnType p_213386_3_, @Nullable SpawnGroupData p_213386_4_, @Nullable CompoundTag p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableMob.AgeableMobGroupData(false);
      }

      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   @Override
   public ItemStack getPickedResult(HitResult target) {
      return new ItemStack(AAItems.IIWI_SPAWN_EGG.get());
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.15D));
      this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.15D));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FLYING_SPEED, 0.4F).add(Attributes.MOVEMENT_SPEED, 0.2F);
   }

   protected PathNavigation createNavigation(Level p_175447_1_) {
      FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, p_175447_1_);
      flyingpathnavigator.setCanOpenDoors(false);
      flyingpathnavigator.setCanFloat(true);
      flyingpathnavigator.setCanPassDoors(true);
      return flyingpathnavigator;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntityDimensions p_213348_2_) {
      return p_213348_2_.height * 0.75F;
   }

   public void aiStep() {
      super.aiStep();
      this.calculateFlapping();
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)((double)this.flapSpeed + (double)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3D);
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround() && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)((double)this.flapping * 0.9D);
      Vec3 vector3d = this.getDeltaMovement();
      if (!this.onGround() && vector3d.y < 0.0D) {
         this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
      }

      this.flap += this.flapping * 2.0F;
   }

   public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      if (!this.isTame() && itemstack.is(ItemTags.FLOWERS)) {
         if (!p_230254_1_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }

         if (!this.level().isClientSide) {
            if (this.random.nextInt(10) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
               this.tame(p_230254_1_);
               this.level().broadcastEntityEvent(this, (byte)7);
            } else {
               this.level().broadcastEntityEvent(this, (byte)6);
            }
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (!this.isFlying() && this.isTame() && this.isOwnedBy(p_230254_1_)) {
         if (!this.level().isClientSide) {
            this.setOrderedToSit(!this.isOrderedToSit());
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (itemstack.getItem() == Items.HONEY_BOTTLE && isTame() && getHealth() < getMaxHealth()) {
         this.heal(4);

         if (!p_230254_1_.getAbilities().instabuild) {
            itemstack.shrink(1);
            p_230254_1_.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
         }

         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         for (int i = 0; i < 4; i++) {
            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return false;
   }

   public static boolean checkHoneycreeperSpawnRules(EntityType<Iiwi> p_223317_0_, LevelAccessor p_223317_1_, MobSpawnType p_223317_2_, BlockPos p_223317_3_, RandomSource p_223317_4_) {
      BlockState blockstate = p_223317_1_.getBlockState(p_223317_3_.below());
      return (blockstate.is(BlockTags.LEAVES) || blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(BlockTags.LOGS) || blockstate.is(Blocks.AIR)) && p_223317_1_.getRawBrightness(p_223317_3_, 0) > 8;
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource source) {
      return false;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   @Override
   public boolean canMate(Animal p_70878_1_) {
      return true;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AAEntities.IIWI.get().create(p_241840_1_);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return AASounds.HONEYCREEPER_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return AASounds.HONEYCREEPER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AASounds.HONEYCREEPER_DEATH.get();
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
   }

   public float getVoicePitch() {
      return getPitch(this.random);
   }

   public static float getPitch(RandomSource p_192000_0_) {
      return (p_192000_0_.nextFloat() - p_192000_0_.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   public boolean isPushable() {
      return true;
   }

   protected void doPush(Entity p_82167_1_) {
      if (!(p_82167_1_ instanceof Player)) {
         super.doPush(p_82167_1_);
      }
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.setOrderedToSit(false);
         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (0.5F * this.getEyeHeight()), this.getBbWidth() * 0.4F);
   }

   @Override
   public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));
   }

   private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
      if (isInSittingPose()) {
         event.setAnimation(AAAnimations.SIT);
         return PlayState.CONTINUE;
      }

      if (isFlying()) {
         event.setAnimation(AAAnimations.FLY);
      }
      else {
         event.setAnimation(AAAnimations.IDLE);
      }

      return PlayState.CONTINUE;
   }

   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return cache;
   }
}
