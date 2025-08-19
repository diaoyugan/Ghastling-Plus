package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.diaoyugan.ghastling_plus.HappyGhastData;

@Mixin(PassiveEntity.class)
public abstract class MixinPassiveEntity_AgeFreeze {

   @Unique private int gh$prevAge;

   @Inject(method = "tickMovement", at = @At("HEAD"))
   private void gh$captureAge(CallbackInfo ci) {
      gh$prevAge = ((PassiveEntity)(Object)this).getBreedingAge();
   }

   @Inject(method = "tickMovement", at = @At("TAIL"))
   private void gh$restoreIfPaused(CallbackInfo ci) {
      Object self = this;
      if (self instanceof HappyGhastEntity e) {
          boolean paused = e.getDataTracker().get(HappyGhastData.AGE_PAUSED);
         if (paused) {
            e.setBreedingAge(gh$prevAge);
         }
      }
   }
}