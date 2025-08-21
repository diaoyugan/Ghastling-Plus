package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.HappyGhastEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HappyGhastEntity.class)
public interface HappyGhastAccessor {
    @Accessor("STAYING_STILL")
    static TrackedData<Boolean> gh_getStayingStill() {
        throw new AssertionError();
    }
}
