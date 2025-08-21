package top.diaoyugan.ghastling_plus;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.util.math.Vec3d;
import top.diaoyugan.ghastling_plus.mixin.HappyGhastAccessor;
import top.diaoyugan.ghastling_plus.mixin.MobEntityAccessor;

import java.util.EnumSet;

public class StayGoal extends Goal {
    private final MobEntity mob;
    private final NoopMoveControl noop;
    private MoveControl original;
    private boolean replaced = false;

    public StayGoal(MobEntity mob) {
        this.mob = mob;
        this.noop = new NoopMoveControl(mob);
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private boolean isStaying() {
        if (!(mob instanceof HappyGhastEntity)) return false;
        return mob.getDataTracker().get(HappyGhastAccessor.gh_getStayingStill());
    }

    @Override
    public boolean canStart() {
        return isStaying();
    }

    @Override
    public boolean shouldContinue() {
        return isStaying();
    }

    @Override
    public void start() {
        stopNavigation();
        swapMoveControl();
    }

    @Override
    public void tick() {
        stopNavigation();
    }

    @Override
    public void stop() {
        restoreMoveControl();
    }

    /** 停掉导航并清除速度 */
    private void stopNavigation() {
        if (mob.getNavigation() != null) mob.getNavigation().stop();
        mob.setVelocity(Vec3d.ZERO);
    }

    /** 替换为 NoopMoveControl */
    private void swapMoveControl() {
        if (replaced) return;
        MobEntityAccessor accessor = (MobEntityAccessor) mob;
        original = accessor.gh_getMoveControl();
        if (original != null) {
            noop.setOriginal(original);
            accessor.gh_setMoveControl(noop);
            replaced = true;
        }
    }

    /** 恢复原始 MoveControl */
    private void restoreMoveControl() {
        if (!replaced || original == null) return;
        MobEntityAccessor accessor = (MobEntityAccessor) mob;
        accessor.gh_setMoveControl(original);
        replaced = false;
    }
}
