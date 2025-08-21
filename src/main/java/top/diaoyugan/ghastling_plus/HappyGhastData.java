package top.diaoyugan.ghastling_plus;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.HappyGhastEntity;

public final class HappyGhastData {
   public static final TrackedData<Boolean> SADDLED = DataTracker.registerData(HappyGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   public static final TrackedData<Boolean> AGE_PAUSED = DataTracker.registerData(HappyGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   public static final TrackedData<Boolean> STAYING = DataTracker.registerData(HappyGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

   private HappyGhastData() {
   }

   public static void init() {
      //用于强加载类
   }
}
