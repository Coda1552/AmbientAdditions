package codyhuh.ambientadditions.common.blocks;

import codyhuh.ambientadditions.common.block_entities.CrateBlockEntity;
import codyhuh.ambientadditions.registry.AABlockEntities;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CrateBlock extends BaseEntityBlock {
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public CrateBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FULL, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AABlockEntities.CRATE.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (state.getValue(FULL) && level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
            ItemStack stack = new ItemStack(AAItems.CRATE.get());


            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(stack)) player.drop(stack, true);
                else player.addItem(stack);
            }

            level.levelEvent(2001, pos, getId(level.getBlockState(pos)));
            level.removeBlock(pos, false);
            releaseEntity(crate, level, pos, Direction.getRandom(level.getRandom()));
        }
        return super.use(state, level, pos, player, hand, result);
    }

    private static InteractionResult releaseEntity(CrateBlockEntity crate, Level level, BlockPos pos, Direction direction) {
        EntityType<?> type = EntityType.by(crate.getCreatureData()).orElse(null);
        LivingEntity entity;

        if (level.isClientSide() && type == null || (entity = (LivingEntity) type.create(level)) == null) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            UUID id = entity.getUUID();
            entity.deserializeNBT(crate.getCreatureData());
            entity.setUUID(id);
            entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 0.0F, 0.0F);

            level.addFreshEntity(entity);
            level.playSound(null, entity.blockPosition(), SoundEvents.BARREL_OPEN, SoundSource.PLAYERS, 1, 1);

            entity.getPersistentData().putBoolean("IsSedated", false);

            level.removeBlockEntity(pos);
            crate.setRemoved();
        }

        return InteractionResult.sidedSuccess(true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FULL);
    }
}
