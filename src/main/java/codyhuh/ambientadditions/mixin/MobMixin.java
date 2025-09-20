package codyhuh.ambientadditions.mixin;

import codyhuh.ambientadditions.common.items.CrateItem;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    @Shadow @Final private Map<BlockPathTypes, Float> pathfindingMalus;

    protected MobMixin(EntityType<? extends LivingEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "Lnet/minecraft/world/entity/Mob;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
    public void druids_n_dinosaurs_interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
         ItemStack stack = player.getItemInHand(hand);

         if (stack.is(AAItems.CRATE.get()) && !CrateItem.containsEntity(stack)) {
             ((CrateItem)stack.getItem()).successfulCrate(player, this, stack, player.level());
         }
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow
    public abstract void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_);

    @Shadow
    public abstract HumanoidArm getMainArm();
}
