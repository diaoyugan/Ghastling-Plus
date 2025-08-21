package top.diaoyugan.ghastling_plus;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;

public class NoopMoveControl extends MoveControl {
    private MoveControl original;

    public NoopMoveControl(MobEntity mob) {
        super(mob);
    }

    public void setOriginal(MoveControl orig) { this.original = orig; }

    @Override
    public void tick() {
        // 故意空：阻止任何移动命令生效
    }

    public MoveControl getOriginal() {
        return original;
    }
}