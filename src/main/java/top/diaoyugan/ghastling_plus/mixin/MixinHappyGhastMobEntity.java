package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.diaoyugan.ghastling_plus.HappyGhastData;

@Mixin(HappyGhastEntity.class)
public class MixinHappyGhastMobEntity extends MobEntity {
    protected MixinHappyGhastMobEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "onGrowUp", at = @At("HEAD"), cancellable = true)
    private void gh$pauseAge(CallbackInfo ci) {
        if (this.getDataTracker().get(HappyGhastData.AGE_PAUSED)) {
            ci.cancel();
        }
    }
}
