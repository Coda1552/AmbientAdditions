package codyhuh.ambientadditions.common.entities;

import codyhuh.ambientadditions.common.entities.util.AAAnimations;
import codyhuh.ambientadditions.registry.AAEntities;
import codyhuh.ambientadditions.registry.AAItems;
import codyhuh.ambientadditions.registry.AASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class CardiganCorgi extends TamableAnimal implements GeoEntity {
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(CardiganCorgi.class, EntityDataSerializers.INT);

   public CardiganCorgi(EntityType<? extends CardiganCorgi> p_i50240_1_, Level p_i50240_2_) {
      super(p_i50240_1_, p_i50240_2_);
      this.setTame(false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 10.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(p_70037_1_.getInt("CollarColor")));
      }
   }

   protected SoundEvent getAmbientSound() {
      return AASounds.CORGI_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WOLF_DEATH;
   }

   protected float getSoundVolume() {
      return 0.2F;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntityDimensions p_213348_2_) {
      return p_213348_2_.height * 0.8F;
   }

   public int getMaxHeadXRot() {
      return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            p_70097_2_ = (p_70097_2_ + 1.0F) / 2.0F;
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = p_70652_1_.hurt(this.level().damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_70652_1_);
      }

      return flag;
   }

   public void setTame(boolean p_70903_1_) {
      super.setTame(p_70903_1_);
      if (p_70903_1_) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
         this.setHealth(20.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
      }
   }

   public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
      ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
      Item item = itemstack.getItem();
      if (this.level().isClientSide) {
         boolean flag = this.isOwnedBy(p_230254_1_) || this.isTame() || item == Items.BONE && !this.isTame();
         return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
      } else {
         if (this.isTame()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
               if (!p_230254_1_.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               this.heal((float)item.getFoodProperties().getNutrition());
               return InteractionResult.SUCCESS;
            }

            if (!(item instanceof DyeItem)) {
               InteractionResult actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
               if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(p_230254_1_)) {
                  this.setOrderedToSit(!this.isOrderedToSit());
                  this.jumping = false;
                  this.navigation.stop();
                  return InteractionResult.SUCCESS;
               }

               return actionresulttype;
            }

            DyeColor dyecolor = ((DyeItem)item).getDyeColor();
            if (dyecolor != this.getCollarColor()) {
               this.setCollarColor(dyecolor);
               if (!p_230254_1_.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               return InteractionResult.SUCCESS;
            }
         } else if (item == Items.PUMPKIN_PIE) {
            if (!p_230254_1_.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
               this.tame(p_230254_1_);
               this.navigation.stop();
               this.setTarget(null);
               this.setOrderedToSit(true);
               this.level().broadcastEntityEvent(this, (byte)7);
            } else {
               this.level().broadcastEntityEvent(this, (byte)6);
            }

            return InteractionResult.SUCCESS;
         }

         return super.mobInteract(p_230254_1_, p_230254_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAngle() {
      return this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
   }

   public boolean isFood(ItemStack p_70877_1_) {
      Item item = p_70877_1_.getItem();
      return item.isEdible() && item.getFoodProperties().isMeat();
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_175547_1_) {
      this.entityData.set(DATA_COLLAR_COLOR, p_175547_1_.getId());
   }

   public CardiganCorgi getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      CardiganCorgi corgi = AAEntities.CARDIGAN_CORGI.get().create(p_241840_1_);
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         corgi.setOwnerUUID(uuid);
         corgi.setTame(true);
      }

      return corgi;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (0.6F * this.getEyeHeight()), (this.getBbWidth() * 0.4F));
   }

   @Override
   public ItemStack getPickedResult(HitResult target) {
      return new ItemStack(AAItems.CARDIGAN_CORGI_SPAWN_EGG.get());
   }

   @Override
   public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
   }

   private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
      if (isInSittingPose()) {
         event.setAnimation(AAAnimations.SIT);
         event.getController().setAnimationSpeed(1.0);
      }
      else if (event.isMoving()) {
         event.setAnimation(AAAnimations.WALK);
         event.getController().setAnimationSpeed(2.5);
      }
      else {
         event.setAnimation(AAAnimations.IDLE);
         event.getController().setAnimationSpeed(1.0);
      }

      return PlayState.CONTINUE;
   }

   private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return factory;
   }
}
