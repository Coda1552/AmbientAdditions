package codyhuh.ambientadditions.common;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.block_entities.CrateBlockEntity;
import codyhuh.ambientadditions.common.blocks.CrateBlock;
import codyhuh.ambientadditions.common.entities.RubberDuckyIsopod;
import codyhuh.ambientadditions.common.entities.util.AbstractFrog;
import codyhuh.ambientadditions.data.SedationData;
import codyhuh.ambientadditions.data.SedationProvider;
import codyhuh.ambientadditions.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = AmbientAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void lowerStunWhenHit(LivingHurtEvent e) {
        LivingEntity living = e.getEntity();
        CompoundTag tag = living.getPersistentData();
        var cap = living.getCapability(SedationProvider.SEDATION_CAP);

        if (e.getAmount() > 0.0F && living.level() instanceof ServerLevel && living instanceof PathfinderMob mob) {
            if (cap.isPresent()) {
                var provider = cap.resolve().isPresent() ? cap.resolve().get() : null;

                if (provider != null) {
                    provider.setLevel(0);
                    tag.putBoolean("IsSedated", false);
                    mob.goalSelector.enableControlFlag(Goal.Flag.LOOK);
                    mob.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                    mob.goalSelector.enableControlFlag(Goal.Flag.JUMP);
                }
            }
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesAnimal(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof PathfinderMob living) {
            if (!living.getCapability(SedationProvider.SEDATION_CAP).isPresent()) {
                e.addCapability(new ResourceLocation(AmbientAdditions.MOD_ID, "sedation"), new SedationProvider());
            }
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent e) {
        e.register(SedationData.class);
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof PathfinderMob living && living.getPersistentData().get("IsSedated") == null) {
            living.getPersistentData().putBoolean("IsSedated", false);
        }
    }

    @SubscribeEvent
    public static void entityTick(LivingEvent.LivingTickEvent e) {
        LivingEntity living = e.getEntity();

        CompoundTag tag = living.getPersistentData();
        var cap = living.getCapability(SedationProvider.SEDATION_CAP);

        if (living.level() instanceof ServerLevel serverLevel && living instanceof PathfinderMob mob) {
            if (cap.isPresent()) {
                var provider = cap.resolve().isPresent() ? cap.resolve().get() : null;

                if (provider != null) {
                    int i = provider.getTimer();

                    if (i == 0) {
                        provider.setLevel(0);
                        tag.putBoolean("IsSedated", false);
                        mob.goalSelector.enableControlFlag(Goal.Flag.LOOK);
                        mob.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                        mob.goalSelector.enableControlFlag(Goal.Flag.JUMP);
                    }
                    else if (i > 0) {
                        provider.setTimer(i - 1);
                    }

                    if (tag.getBoolean("IsSedated")) {
                        zzzParticles(living, 30, serverLevel);
                        mob.getNavigation().stop();
                        mob.goalSelector.disableControlFlag(Goal.Flag.LOOK);
                        mob.goalSelector.disableControlFlag(Goal.Flag.MOVE);
                        mob.goalSelector.disableControlFlag(Goal.Flag.JUMP);
                    }
                }
            }
        }
    }

    private static void zzzParticles(LivingEntity entity, int amount, ServerLevel level) {
        if (entity.tickCount % amount == 0) {
            level.sendParticles(AAParticles.ZZZ.get(), getHeadOffset(entity).x(), getHeadOffset(entity).y(), getHeadOffset(entity).z(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public static Vec3 getHeadOffset(LivingEntity entity) {
        return getYawVec(entity.getYRot(), 0.0F, entity.getEyeHeight() + (entity.getBbHeight() * 0.3F), entity.getBbWidth() * 0.5F).add(entity.position());
    }

    public static Vec3 getYawVec(float yaw, double xOffset, double yOffset, double zOffset) {
        return new Vec3(xOffset, yOffset, zOffset).yRot(-yaw * (Mth.PI / 180f));
    }

    @SubscribeEvent
    public static void frogBreed(BabyEntitySpawnEvent e) {
        if (e.getParentA() instanceof AbstractFrog parentA && e.getParentB() instanceof AbstractFrog parentB) {
            parentA.setGravid(true);
            parentB.getNavigation().stop();
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent e) {
        Player player = e.getPlayer();
        BlockState state = e.getState();
        LevelAccessor level = e.getLevel();
        BlockPos pos = e.getPos();

        if (state.is(AABlocks.CRATE.get())) {
            ItemStack crateItem = new ItemStack(AAItems.CRATE.get());

            if (state.getValue(CrateBlock.FULL)) {
                if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {

                    CompoundTag tag = crateItem.getOrCreateTag();
                    tag.put("CreatureData", crate.getCreatureData());
                    Block.popResource((Level) level, pos, crateItem);
                }
            }
            else {
                if (!player.getAbilities().instabuild) {
                    Block.popResource((Level) level, pos, crateItem);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent e) {
        Entity entity = e.getEntity();
        BlockState state = e.getState();
        Level level = (Level)e.getLevel();
        BlockPos pos = e.getPos();

        if (entity instanceof Player player) {
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());

            CompoundTag targetTag = stack.getOrCreateTag().getCompound("CreatureData");

            if (!targetTag.isEmpty()) {
                level.setBlockEntity(new CrateBlockEntity(pos, AABlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.FULL, true)));
                level.setBlock(pos, AABlocks.CRATE.get().defaultBlockState().setValue(CrateBlock.FULL, true), 3);

                CrateBlockEntity crate = level.getBlockEntity(pos, AABlockEntities.CRATE.get()).get();

                crate.setCreatureData(targetTag, crate.getPersistentData());
            }
        }
    }

    @SubscribeEvent
    public static void onLogStripped(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof AxeItem) {
            Level world = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = world.getBlockState(pos);

            if (state.is(Blocks.JUNGLE_LOG) && world.random.nextInt(40) == 0) {
                RubberDuckyIsopod entity = AAEntities.RUBBER_DUCKY_ISOPOD.get().create(world);

                // this is a horrible way to do this, but it works
                if (world.getBlockState(pos.above()).is(Blocks.AIR)) {
                    pos = pos.above();
                }
                else if (world.getBlockState(pos.below()).is(Blocks.AIR)) {
                    pos = pos.below();
                }
                else if (world.getBlockState(pos.north()).is(Blocks.AIR)) {
                    pos = pos.north();
                }
                else if (world.getBlockState(pos.south()).is(Blocks.AIR)) {
                    pos = pos.south();
                }
                else if (world.getBlockState(pos.east()).is(Blocks.AIR)) {
                    pos = pos.east();
                }
                else if (world.getBlockState(pos.west()).is(Blocks.AIR)) {
                    pos = pos.west();
                }

                entity.moveTo(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                world.addFreshEntity(entity);
            }
        }
    }

    @SubscribeEvent
    public static void addWanderingTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> trades = event.getGenericTrades();

        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 6), new ItemStack(AAItems.HARLEQUIN_SHRIMP_BUCKET.get()), 3, 4, 1.5f));
        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 4), new ItemStack(AAItems.MOLE_BUCKET.get()), 3, 4, 1.5f));
        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(AAItems.CHOCOLATE_CHIP_STARFISH_BUCKET.get()), 3, 4, 1.5f));
        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(AAItems.SHAME_FACED_CRAB_BUCKET.get()), 3, 4, 1.5f));
        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 7), new ItemStack(AAItems.LONGHORN_COWFISH_BUCKET.get()), 3, 4, 1.5f));
        trades.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(AAItems.LEAF_FROG_BOWL.get()), 3, 4, 1.5f));
    }

    private static class ItemsForItemsTrade implements VillagerTrades.ItemListing {
        private final ItemStack buying1, buying2, selling;
        private final int maxUses, xp;
        private final float priceMultiplier;

        public ItemsForItemsTrade(ItemStack buying1, ItemStack buying2, ItemStack selling, int maxUses, int xp, float priceMultiplier) {
            this.buying1 = buying1;
            this.buying2 = buying2;
            this.selling = selling;
            this.maxUses = maxUses;
            this.xp = xp;
            this.priceMultiplier = priceMultiplier;
        }

        public ItemsForItemsTrade(ItemStack buying1, ItemStack selling, int maxUses, int xp, float priceMultiplier) {
            this(buying1, ItemStack.EMPTY, selling, maxUses, xp, priceMultiplier);
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource rand) {
            return new MerchantOffer(buying1, buying2, selling, maxUses, xp, priceMultiplier);
        }
    }
}
