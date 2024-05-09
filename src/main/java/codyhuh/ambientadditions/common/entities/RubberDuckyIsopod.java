package codyhuh.ambientadditions.common.entities;

import codyhuh.ambientadditions.common.entities.ai.goal.AvoidEntityWithoutMaskGoal;
import codyhuh.ambientadditions.common.entities.util.AAAnimations;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;

public class RubberDuckyIsopod extends PathfinderMob implements GeoEntity {

    public RubberDuckyIsopod(EntityType<? extends PathfinderMob> p_i48568_1_, Level p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(1, new AvoidEntityWithoutMaskGoal<>(this, Player.class, 2.0F, 1.5D, 2.25D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(AAItems.RUBBER_DUCKY_ISOPOD_SPAWN_EGG.get());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        BlockState blockstate = this.level().getBlockState(pos.above());
        boolean flag = blockstate.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS);
        if (flag || !(state.getBlock() instanceof IFluidBlock)) {
            SoundType soundtype = blockstate.is(Blocks.SNOW) ? blockstate.getSoundType(level(), pos, this) : state.getSoundType(level(), pos, this);
            this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.025F, soundtype.getPitch());
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        Level world = getCommandSenderWorld();
        Map<ItemStack, ItemStack> strippedLogs = Map.of();

        if (item == AAItems.BARK.get()) {
            if (random.nextFloat() > 0.9F) {
                ItemEntity molt = EntityType.ITEM.create(world);
                molt.setItem(new ItemStack(AAItems.ISOPOD_MOLT.get()));
                molt.moveTo(getX(), getY(), getZ());
                world.addFreshEntity(molt);
            }

            player.playSound(SoundEvents.FOX_BITE, 0.4F, 1.0F);

            double d0 = getX() + this.random.nextGaussian() * 0.02D;
            double d1 = getY() + this.random.nextGaussian() * 0.02D;
            double d2 = getZ() + this.random.nextGaussian() * 0.02D;
            Vec3 vector3d = (new Vec3(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.getXRot() * ((float)Math.PI / 180F)).yRot(-this.getYRot() * ((float)Math.PI / 180F));
            world.addParticle(new ItemParticleOption(ParticleTypes.ITEM,  new ItemStack(AAItems.BARK.get())), d0, d1, d2, vector3d.x, vector3d.y + 0.05D, vector3d.z);

            player.swing(hand);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.24D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));
    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving()) {
            event.setAnimation(AAAnimations.WALK);
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
