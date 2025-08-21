package top.diaoyugan.ghastling_plus.mixin;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
    // 改名为不太可能冲突的 gh_getMoveControl / gh_setMoveControl
    @Accessor("moveControl")
    MoveControl gh_getMoveControl();

    @Accessor("moveControl")
    void gh_setMoveControl(MoveControl mc);

    // 改名为 gh_getGoalSelector，避免与目标类方法名冲突
    @Accessor("goalSelector")
    GoalSelector gh_getGoalSelector();
}
