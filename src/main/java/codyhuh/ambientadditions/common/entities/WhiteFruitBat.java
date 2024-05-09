package codyhuh.ambientadditions.common.entities;

import codyhuh.ambientadditions.common.entities.ai.goal.BatLandOnGroundGoal;
import codyhuh.ambientadditions.common.entities.ai.goal.BatWanderGoal;
import codyhuh.ambientadditions.common.entities.ai.goal.MoveToLeavesGoal;
import codyhuh.ambientadditions.common.entities.ai.movement.GroundAndFlyingMoveControl;
import codyhuh.ambientadditions.common.entities.util.AAAnimations;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WhiteFruitBat extends Animal implements FlyingAnimal, GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(WhiteFruitBat.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(WhiteFruitBat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_LANDING = SynchedEntityData.defineId(WhiteFruitBat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FLIGHT_TICKS = SynchedEntityData.defineId(WhiteFruitBat.class, EntityDataSerializers.INT);
    public BatWanderGoal wanderGoal;
    public BatLandOnGroundGoal landGoal;
    public float prevTilt;
    public float tilt;

    public WhiteFruitBat(EntityType<? extends WhiteFruitBat> type, Level worldIn) {
        super(type, worldIn);
        this.setResting(true);
        this.moveControl = new GroundAndFlyingMoveControl(this, 60, 12000);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.025D).add(Attributes.FLYING_SPEED, 0.55D);
    }

    @Override
    protected void registerGoals() {
        landGoal = new BatLandOnGroundGoal(this, 1.0D);
        wanderGoal = new BatWanderGoal(this, 1.0D, 1.0F);
        this.goalSelector.addGoal(0, new MoveToLeavesGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, landGoal);
        this.goalSelector.addGoal(6, wanderGoal);
    }

    @Override
    public void travel(Vec3 vec3d) {
        boolean flying = this.isFlying();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);

        if (flying) {
            this.moveRelative(speed, vec3d);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setDeltaMovement(getDeltaMovement().scale(0.91f));
            this.calculateEntityAnimation(true);
        }
        else {
            super.travel(vec3d);
        }
    }

    public boolean wantsToFly() {
        return canFly() && getFlightTicks() <= 12000;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(AAItems.WHITE_FRUIT_BAT_SPAWN_EGG.get());
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.05F;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return null;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 0.3F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte) 0);
        this.entityData.define(IS_FLYING, false);
        this.entityData.define(FLIGHT_TICKS, 0);
        this.entityData.define(IS_LANDING, false);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity p_82167_1_) {
    }

    @Override
    protected void pushEntities() {
    }

    public boolean isResting() {
        return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean p_82236_1_) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (p_82236_1_) {
            this.entityData.set(DATA_ID_FLAGS, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte) (b0 & -2));
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(DATA_ID_FLAGS, pCompound.getByte("BatFlags"));
        this.setFlying(pCompound.getBoolean("IsBatFlying"));
        this.setFlightTicks(pCompound.getInt("FlightTicks"));
        this.setLanding(pCompound.getBoolean("IsLanding")); }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
        pCompound.putBoolean("IsBatFlying", this.isFlying());
        pCompound.putInt("FlightTicks", this.getFlightTicks());
        pCompound.putBoolean("IsLanding", this.isLanding());
    }

    public boolean isFlying() {
        return this.entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(IS_FLYING, flying);
    }

    public boolean isLanding() {
        return this.entityData.get(IS_LANDING);
    }

    public void setLanding(boolean landing) {
        this.entityData.set(IS_LANDING, landing);
    }

    public int getFlightTicks() {
        return this.entityData.get(FLIGHT_TICKS);
    }

    public void setFlightTicks(int flightTicks) {
        this.entityData.set(FLIGHT_TICKS, flightTicks);
    }

    public boolean canFly() {
        BlockPos pos = blockPosition();

        return !level().getBlockState(pos.offset(0, -1, 0)).isSolid();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide && this.isResting()) {
            this.setResting(false);
        }
        if (wanderGoal != null) {
            wanderGoal.trigger();
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void setNoGravity(boolean pNoGravity) {
        super.setNoGravity(isFlying() || isLanding());
    }

    @Override
    public void tick() {
        super.tick();

        if (level().getBlockState(blockPosition().below(1)).isAir() && !isFlying() && !isLanding()) {
            if (landGoal != null) {
                landGoal.trigger();
            }
        }

        if (getFlightTicks() <= 12000 && (isFlying() || isLanding()) && !isNoAi()) {
            setFlightTicks(getFlightTicks() + 1);
        }

        if (onGround() || getFlightTicks() >= 12000 || isUnderWater()) {
            setFlying(false);
        }

        if (onGround() && isLanding()) {
            setLanding(false);
        }

        if (getFlightTicks() > 0 && !isFlying()) {
            setFlightTicks(getFlightTicks() - 1);
        }

        double x = getDeltaMovement().x();
        double z = getDeltaMovement().z();

        boolean notMoving = Math.abs(x) < 0.1D && Math.abs(z) < 0.1D;

        if (wanderGoal != null && isFlying() && !isLanding() && wantsToFly() && notMoving) {
            wanderGoal.trigger();
        }

        if (onGround()) {
            setResting(true);
        } else if (isInWater()) {
            setResting(false);
            if (getPersistentData().getBoolean("IsSedated")) {
                getPersistentData().putBoolean("IsSedated", false);
            }
        } else if (this.isResting()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPosRaw(this.getX(), Mth.floor(this.getY()), this.getZ());
            if (level().getBlockState(blockPosition().below()).isAir()) {
                setResting(false);
                if (getPersistentData().getBoolean("IsSedated")) {
                    getPersistentData().putBoolean("IsSedated", false);
                }
            }
        } else {
            if (getPersistentData().getBoolean("IsSedated")) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.25D, 0.0D, 0.25D).subtract(0.0D, 0.5D, 0.0D));

                if (onGround()) {
                    setResting(true);
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        prevTilt = tilt;
        if (!onGround()) {
            final float v = Mth.degreesDifference(this.getYRot(), yRotO);
            if (Math.abs(v) > 1) {
                if (Math.abs(tilt) < 25) {
                    tilt += Math.signum(v);
                }
            } else {
                if (Math.abs(tilt) > 0) {
                    final float tiltSign = Math.signum(tilt);
                    tilt -= tiltSign * 3;
                    if (tilt * tiltSign < 0) {
                        tilt = 0;
                    }
                }
            }
        } else {
            tilt = 0;
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPos blockpos = this.blockPosition();
        BlockPos blockpos1 = blockpos.below();
        if (this.level().getBlockState(blockpos1).is(BlockTags.LEAVES)) {
            this.setResting(true);
        } else if (this.isResting()) {
            boolean flag = this.isSilent();
            if (!this.level().getBlockState(blockpos1).isAir() || getPersistentData().getBoolean("IsSedated")) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float) this.random.nextInt(360);
                }
            } else {
                this.setResting(false);
                if (!flag) {
                    this.level().levelEvent(null, 1025, blockpos, 0);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));
    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving()) {
            event.setAnimation(AAAnimations.FLY);
            event.getController().setAnimationSpeed(2.0D);
        } else {
            event.setAnimation(AAAnimations.SIT);
            event.getController().setAnimationSpeed(1.0D);
        }

        return PlayState.CONTINUE;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}