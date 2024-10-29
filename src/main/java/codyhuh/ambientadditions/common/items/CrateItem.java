package codyhuh.ambientadditions.common.items;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.data.SedationProvider;
import codyhuh.ambientadditions.registry.AAItems;
import codyhuh.ambientadditions.registry.AATags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CrateItem extends Item {
    public static final String DATA_CREATURE = "CreatureData";

    public CrateItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();

        if (containsEntity(stack)) return InteractionResult.PASS;

        if (!target.getPassengers().isEmpty()) target.ejectPassengers();

        var cap = target.getCapability(SedationProvider.SEDATION_CAP);

        int sedationLevel = cap.resolve().isPresent() ? cap.resolve().get().getLevel() : 0;

        if (canBeCrated(target) && target.getPersistentData().getBoolean("IsSedated") && sedationLevel >= AmbientAdditions.sedationLvlRequiredToCapture(target.getMaxHealth())) {

            if (target instanceof TamableAnimal tame && tame.isTame()) {
                return unsuccessfulCrate(tame, level);
            }
            else {
                return successfulCrate(target, player, hand, stack, level);
            }

        }

        return InteractionResult.sidedSuccess(true);
    }

    public InteractionResult successfulCrate(LivingEntity target, Player player, InteractionHand hand, ItemStack stack, Level level) {
        ItemStack stack1 = player.getItemInHand(hand);

        boolean more = stack.getCount() > 1;

        if (more) {
            stack1 = new ItemStack(AAItems.CRATE.get());
            stack.shrink(1);
        }

        CompoundTag targetTag = target.serializeNBT();
        targetTag.putString("OwnerName", player.getName().getString());
        CompoundTag tag = stack1.getOrCreateTag();
        tag.put(DATA_CREATURE, targetTag);
        stack1.setTag(tag);

        if (more) {
            if (!player.getInventory().add(stack1)) player.drop(stack1, true);
            else player.addItem(stack1);
        }

        target.discard();

        level.playSound(null, player.blockPosition(), SoundEvents.BARREL_CLOSE, SoundSource.AMBIENT, 1, 1);

        if (level instanceof ServerLevel serverLevel) {
            double width = target.getBbWidth();
            for (int i = 0; i <= Math.floor(width) * 25; ++i) {

                for (int j = 0; j < 8; j++) {
                    double x = target.getRandomX(1.0D);
                    double y = target.getRandomY();
                    double z = target.getRandomZ(1.0D);

                    serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult unsuccessfulCrate(LivingEntity target, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            double width = target.getBbWidth();
            for (int i = 0; i <= Math.floor(width) * 25; ++i) {

                for (int j = 0; j < 12; j++) {
                    double x = target.getRandomX(1.0D);
                    double y = target.getRandomY();
                    double z = target.getRandomZ(1.0D);

                    serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.05D);
                }
            }

        }
        return InteractionResult.PASS;
    }

    private boolean canBeCrated(LivingEntity entity) {
        return !(entity instanceof WitherBoss) && !(entity instanceof EnderDragon) && !(entity instanceof Warden) && !entity.getType().is(AATags.UNCRATEABLE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockHitResult rt = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        ItemStack stack = player.getItemInHand(hand);
        if (rt.getType() == HitResult.Type.MISS) return InteractionResultHolder.pass(stack);
        BlockPos pos = rt.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) return InteractionResultHolder.success(stack);
        return new InteractionResultHolder<>(releaseEntity(level, player, stack, pos, rt.getDirection()), stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent name = (MutableComponent) super.getName(stack);
        MutableComponent creatureName = containsEntity(stack) ? EntityType.byString(stack.getTag()
                        .getCompound(DATA_CREATURE)
                        .getString("id"))
                .orElse(null)
                .getDescription().copy() : Component.empty();

        return containsEntity(stack) ? name.copy().append(" of ").append(creatureName) : name;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if (containsEntity(stack)) {
            CompoundTag tag = stack.getTag().getCompound(DATA_CREATURE);
            Component name;

            if (tag.contains("CustomName")) {
                name = Component.Serializer.fromJson(tag.getString("CustomName"));
            }
            else {
                name = EntityType.byString(tag.getString("id")).orElse(null).getDescription().copy().withStyle(ChatFormatting.GRAY);
            }
            tooltip.add(name);

            String entity = EntityType.getKey(EntityType.byString(tag.getString("id")).orElse(null)).getPath();

            Component extraInfo = Component.translatable("tooltip.ambientadditions.fun_fact." + entity).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC);

            if (Screen.hasShiftDown()) {
                if (!extraInfo.getString().equals("tooltip.ambientadditions.fun_fact." + entity)) {
                    tooltip.add(extraInfo);
                }
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return containsEntity(stack);
    }

    public static boolean containsEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(DATA_CREATURE);
    }

    private static InteractionResult releaseEntity(Level level, Player player, ItemStack stack, BlockPos pos, Direction direction) {
        if (!containsEntity(stack)) return InteractionResult.PASS;

        CompoundTag tag = stack.getTag().getCompound(DATA_CREATURE);
        EntityType<?> type = EntityType.byString(tag.getString("id")).orElse(null);
        LivingEntity entity;

        if (type == null || (entity = (LivingEntity) type.create(level)) == null) {
            AmbientAdditions.LOGGER.error("Something went wrong releasing an animal from a Crate!");
            return InteractionResult.FAIL;
        }

        EntityDimensions size = entity.getDimensions(entity.getPose());
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty())
            pos = pos.relative(direction, (int) (direction.getAxis().isHorizontal() ? size.width : 1));

        entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        AABB aabb = entity.getBoundingBox();

        if (!level.noCollision(entity, new AABB(aabb.minX, entity.getEyeY() - 0.35, aabb.minZ, aabb.maxX, entity.getEyeY() + 1.0, aabb.maxZ))) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            UUID id = entity.getUUID();
            entity.deserializeNBT(tag);
            entity.setUUID(id);
            entity.moveTo(pos.getX(), pos.getY() + direction.getStepY() + 1.0, pos.getZ(), player.getYRot(), 0f);

            if (stack.hasCustomHoverName()) entity.setCustomName(stack.getHoverName());
            stack.removeTagKey(DATA_CREATURE);
            level.addFreshEntity(entity);
            level.playSound(null, entity.blockPosition(), SoundEvents.BARREL_OPEN, SoundSource.AMBIENT, 1, 1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else if (context.getItemInHand().hasTag()) {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            ItemStack stack = context.getItemInHand();
            CompoundTag tag = stack.getTag().getCompound(DATA_CREATURE);
            EntityType<?> type = EntityType.byString(tag.getString("id")).orElse(null);
            LivingEntity entity = (LivingEntity) type.create(context.getLevel());
            if (entity == null) return InteractionResult.FAIL;

            UUID id = entity.getUUID();
            entity.deserializeNBT(tag);
            entity.setUUID(id);

            entity.moveTo(blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5, context.getPlayer().getYRot(), 0f);

            if (stack.hasCustomHoverName()) entity.setCustomName(stack.getHoverName());
            stack.removeTagKey(DATA_CREATURE);

            if (context.getLevel().addFreshEntity(entity)) {
                itemstack.shrink(1);
            }
            context.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BARREL_OPEN, SoundSource.AMBIENT, 1, 1);
            context.getPlayer().setItemInHand(context.getHand(), new ItemStack(AAItems.CRATE.get()));

            return InteractionResult.CONSUME;
        }
        else {
            return super.useOn(context);
        }
    }
}