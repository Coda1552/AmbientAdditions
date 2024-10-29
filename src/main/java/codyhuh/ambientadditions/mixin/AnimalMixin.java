package codyhuh.ambientadditions.mixin;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.items.CrateItem;
import codyhuh.ambientadditions.data.SedationProvider;
import codyhuh.ambientadditions.registry.AAItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(Animal.class)
public class AnimalMixin extends PathfinderMob {

    protected AnimalMixin(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    //@Inject(method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
    public void ambientadditions_mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var cap = getCapability(SedationProvider.SEDATION_CAP);

        int sedationLevel = cap.resolve().isPresent() ? cap.resolve().get().getLevel() : 0;

        if (getPersistentData().getBoolean("IsSedated") && sedationLevel >= AmbientAdditions.sedationLvlRequiredToCapture(getMaxHealth())) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(AAItems.CRATE.get()) && CrateItem.containsEntity(stack)) {
                ((CrateItem)stack.getItem()).successfulCrate(this, stack, level());
            }
            else {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
