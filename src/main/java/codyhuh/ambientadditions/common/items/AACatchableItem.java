package codyhuh.ambientadditions.common.items;

import codyhuh.ambientadditions.AmbientAdditions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class AACatchableItem extends BucketItem {
    private final Supplier<? extends EntityType<?>> entityType;
    private final Item item1;
    private final boolean hasTooltip;

    public AACatchableItem(Supplier<EntityType<?>> entityType, Item item, boolean hasTooltip, Properties properties) {
        this(entityType, Fluids.EMPTY, item, hasTooltip, properties);
    }

    public AACatchableItem(Supplier<EntityType<?>> entityType, Fluid fluid, Item item, boolean hasTooltip, Properties properties) {
        super(fluid, properties);
        this.entityType = entityType;
        this.item1 = item;
        this.hasTooltip = hasTooltip;
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> AmbientAdditions.CALLBACKS.add(() -> ItemProperties.register(this, new ResourceLocation(AmbientAdditions.MOD_ID, "variant"), (stack, world, player, i) -> stack.hasTag() ? stack.getTag().getInt("Variant") : 0)));
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();

        if (!world.isClientSide) {
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
            Supplier<? extends EntityType<?>> type = entityType;
            Entity entityType = type.get().spawn((ServerLevel) world, itemstack, player, blockpos1, MobSpawnType.BUCKET, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entityType != null) {
                if (player != null && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    if (context.getItemInHand().isEmpty()) {
                        player.setItemInHand(context.getHand(), new ItemStack(item1));
                    }
                    else {
                        player.addItem(new ItemStack(item1));
                    }
                }

                playEmptySound(player, world, blockpos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (hasTooltip && stack.hasTag()) {
            MutableComponent variant = Component.translatable(getEntityType().getDescriptionId() + "." + stack.getTag().getInt("Variant")).withStyle(ChatFormatting.GRAY);

            tooltip.add(variant);
        }
    }

    protected EntityType<?> getEntityType() {
        return entityType.get();
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        if (getFluid().is(FluidTags.WATER)) {
            worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_AXOLOTL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        else {
            worldIn.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
