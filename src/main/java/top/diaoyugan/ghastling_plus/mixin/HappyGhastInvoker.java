package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.passive.HappyGhastEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HappyGhastEntity.class)
public interface HappyGhastInvoker {
    @Invoker("setStillTimeout")
    void gh_setStillTimeout(int timeout);
}

