package codyhuh.ambientadditions.common.items;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.block_entities.CrateBlockEntity;
import codyhuh.ambientadditions.common.blocks.CrateBlock;
import codyhuh.ambientadditions.data.SedationProvider;
import codyhuh.ambientadditions.registry.AABlocks;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class CrateItem extends BlockItem {
    public static final String DATA_CREATURE = "CreatureData";

    public CrateItem(Properties properties) {
        super(AABlocks.CRATE.get(), properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return containsEntity(stack) ? 1 : super.getMaxStackSize(stack);
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
            } else {
                return successfulCrate(target, stack, level);
            }

        }

        return InteractionResult.sidedSuccess(true);
    }

    public InteractionResult successfulCrate(LivingEntity target, ItemStack stack, Level level) {
        stack.shrink(1);

        CompoundTag targetTag = target.serializeNBT();

        target.discard();

        BlockPos.MutableBlockPos pos = target.blockPosition().mutable();

        while (!level.getBlockState(pos.below()).isSolid()) {
            pos.move(Direction.DOWN);
        }

        level.setBlockEntity(new CrateBlockEntity(pos, AABlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.FULL, true)));
        level.setBlock(pos, AABlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.FULL, true), 3);
        level.playSound(null, target.blockPosition(), SoundEvents.BARREL_CLOSE, SoundSource.PLAYERS, 1, 1);

        if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
            crate.setCreatureData(targetTag, crate.getPersistentData());
        }

        if (level instanceof ServerLevel server) {
            double d0 = 0.5D;
            double d1 = AABlocks.CRATE.get().defaultBlockState().getShape(level, pos).max(Direction.Axis.Y);

            double d2 = level.random.nextGaussian() * 0.02D;
            double d3 = level.random.nextGaussian() * 0.02D;
            double d4 = level.random.nextGaussian() * 0.02D;
            double d6 = (double) pos.getX() + level.random.nextDouble() * d0 * 2.0D;
            double d7 = (double) pos.getY() + level.random.nextDouble() * d1;
            double d8 = (double) pos.getZ() + level.random.nextDouble() * d0 * 2.0D;
            server.sendParticles(ParticleTypes.POOF, d6, d7, d8, 100, d2, d3, d4, 0.1D);
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
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock))
            return InteractionResultHolder.success(stack);
        //return new InteractionResultHolder<>(releaseEntity(level, player, stack, pos, rt.getDirection()), stack);
        return super.use(level, player, hand);
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
            } else {
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

    public static boolean containsEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(DATA_CREATURE);
    }
}