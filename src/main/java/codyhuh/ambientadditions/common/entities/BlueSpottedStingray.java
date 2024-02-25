package codyhuh.ambientadditions.common.entities;

import codyhuh.ambientadditions.common.entities.ai.movement.BottomFeederMoveControl;
import codyhuh.ambientadditions.common.entities.util.AAAnimations;
import codyhuh.ambientadditions.common.entities.util.ISwimmer;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlueSpottedStingray extends AbstractFish implements GeoEntity, ISwimmer {

    public BlueSpottedStingray(EntityType<? extends AbstractFish> p_27461_, Level p_27462_) {
        super(p_27461_, p_27462_);
        this.moveControl = new BottomFeederMoveControl(this);
        this.lookControl = new SmoothSwimmingLookControl(this, 5);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D);
    }

    @Override
    public void playerTouch(Player player) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.hurt(this.level().damageSources().mobAttack(this), 1)) {
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PUFFER_FISH_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PUFFER_FISH_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.PUFFER_FISH_HURT;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(AAItems.BLUE_SPOTTED_STINGRAY_SPAWN_EGG.get());
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AAItems.BLUE_SPOTTED_STINGRAY_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));
    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving()) {
            event.setAnimation(AAAnimations.SWIM);
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
