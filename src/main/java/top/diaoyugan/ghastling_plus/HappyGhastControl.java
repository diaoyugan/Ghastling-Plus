package top.diaoyugan.ghastling_plus;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public final class HappyGhastControl {

   public static void init() {
      // 注册实体交互事件
      UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {

         // 只处理 HappyGhast 实体
         if (!(entity instanceof HappyGhastEntity gh)) {
            return ActionResult.PASS;
         }

         // 客户端直接放行
         if (world.isClient) {
            return ActionResult.PASS;
         }

         ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
         ItemStack stack = player.getStackInHand(hand);

         boolean paused = gh.getDataTracker().get(HappyGhastData.AGE_PAUSED);
         boolean saddled = gh.getDataTracker().get(HappyGhastData.SADDLED);

         // 金苹果 → 暂停生长
         if (stack.isOf(Items.GOLDEN_APPLE)) {
            if (paused) {
               Messages.sendMessage(serverPlayer, Text.translatable("gp.growStopped"), true);
            } else {
               gh.getDataTracker().set(HappyGhastData.AGE_PAUSED, true);
               if (!player.isCreative()) stack.decrement(1);
               Messages.sendMessage(serverPlayer, Text.translatable("gp.growStop"), true);
            }
            return ActionResult.SUCCESS;
         }

         // 糖 → 取消暂停
         if (stack.isOf(Items.SUGAR)) {
            if (!paused) {
               Messages.sendMessage(serverPlayer, Text.translatable("gp.notPaused"), true);
               return ActionResult.PASS;
            }
            gh.getDataTracker().set(HappyGhastData.AGE_PAUSED, false);
            if (!player.isCreative()) stack.decrement(1);
            Messages.sendMessage(serverPlayer, Text.translatable("gp.unpaused"), true);
            return ActionResult.SUCCESS;
         }

         // 骨头 → 倒退生长
         if (stack.isOf(Items.BONE)) {
            if (paused) {
               Messages.sendMessage(serverPlayer, Text.translatable("gp.cannotRevert"), true);
            } else {
               gh.growUp(-60, true);
               if (!player.isCreative()) stack.decrement(1);
               Messages.sendMessage(serverPlayer, Text.translatable("gp.ageReverted"), true);
            }
            return ActionResult.SUCCESS;
         }

         // 雪球 → 交互
         if (stack.isOf(Items.SNOWBALL)) {
            if (paused) {
               Messages.sendMessage(serverPlayer, Text.translatable("gp.snowballPaused"), true);
               return ActionResult.SUCCESS;
            }
            return gh.interact(player, hand);
         }

         // 拴绳 → 保持正常使用
         if (stack.isOf(Items.LEAD)) {
            int originalCount = stack.getCount();
            ActionResult result = gh.interact(player, hand);
            if (player.getAbilities().creativeMode) stack.setCount(originalCount);
            return result;
         }

         // 已装备鞍 → 直接骑乘
         if (saddled) {
            if (!gh.hasPassengers()) {
               player.startRiding(gh);
            }
            return ActionResult.SUCCESS;
         }

         // 鞍 → 装鞍并骑乘
         if (stack.isOf(Items.SADDLE)) {
            if (!gh.isBaby()) {
               Messages.sendMessage(serverPlayer, Text.translatable("gp.onlyBabySaddle"), true);
               return ActionResult.PASS;
            }
            gh.getDataTracker().set(HappyGhastData.SADDLED, true);
            if (!player.isCreative()) stack.decrement(1);
            player.startRiding(gh);
            Messages.sendMessage(serverPlayer, Text.translatable("gp.saddledAndRidden"), true);
            return ActionResult.SUCCESS;
         }

         return ActionResult.PASS;
      });
   }
}