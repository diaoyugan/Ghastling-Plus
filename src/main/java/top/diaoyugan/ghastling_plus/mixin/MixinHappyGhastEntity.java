package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.diaoyugan.ghastling_plus.HappyGhastData;

@Mixin(HappyGhastEntity.class)
public abstract class MixinHappyGhastEntity {

   // 你原有的三段照旧……
   @Inject(method = "initDataTracker", at = @At("TAIL"))
   private void gh$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
      builder.add(HappyGhastData.SADDLED, false);
      builder.add(HappyGhastData.AGE_PAUSED, false);
   }

   @Inject(method = "writeCustomData", at = @At("TAIL"))
   private void gh$writeCustomData(WriteView nbt, CallbackInfo ci) {
      Entity self = (Entity)(Object)this;
      nbt.putBoolean("GhSaddled", self.getDataTracker().get(HappyGhastData.SADDLED));
      nbt.putBoolean("GhAgePaused", self.getDataTracker().get(HappyGhastData.AGE_PAUSED));
   }

   @Inject(method = "readCustomData", at = @At("TAIL"))
   private void gh$readCustomData(ReadView nbt, CallbackInfo ci) {
      Entity self = (Entity)(Object)this;
      self.getDataTracker().set(HappyGhastData.SADDLED, nbt.getBoolean("GhSaddled", false));
      self.getDataTracker().set(HappyGhastData.AGE_PAUSED, nbt.getBoolean("GhAgePaused", false));
   }

   // 只在“由幼年变为成年”的那一刻触发：卸鞍 + 踢乘客（只执行一次）
   @Inject(method = "onGrowUp", at = @At("TAIL"))
   private void gh$adultUnsaddleAndDismount(CallbackInfo ci) {
      HappyGhastEntity self = (HappyGhastEntity)(Object)this;

      // 只在成年时处理；回退成幼年（如果有）不触发
      if (!self.isBaby() && !self.getWorld().isClient()) {
         var tracker = self.getDataTracker();

         // 卸鞍（如果当前有鞍）
         if (tracker.get(HappyGhastData.SADDLED)) {
            tracker.set(HappyGhastData.SADDLED, false);
         }

         // 若此刻有人在骑，踢下（只会在这一瞬间做一次）
         if (self.hasPassengers()) {
            self.removeAllPassengers();
         }
      }
   }
}
