package top.diaoyugan.ghastling_plus;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HappyGhastControl {

   private static final Map<UUID, Long> LAST_INTERACT_TICK = new ConcurrentHashMap<>();

   public static void init() {
      UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {

         if (!canProcess(player, world, hand, entity)) return ActionResult.PASS;

         ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
         HappyGhastEntity gh = (HappyGhastEntity) entity;
         ItemStack stack = player.getStackInHand(hand);

         // 防抖
         if (!shouldProcessInteraction(player.getUuid(), world.getTime())) return ActionResult.PASS;

         boolean paused = gh.getDataTracker().get(HappyGhastData.AGE_PAUSED);
         boolean saddled = gh.getDataTracker().get(HappyGhastData.SADDLED);

         // 处理物品交互
         if (stack.isOf(Items.GOLDEN_APPLE)) return handleGoldenApple(gh, stack, serverPlayer, paused);
         if (stack.isOf(Items.SUGAR)) return handleSugar(gh, stack, serverPlayer, paused);
         if (stack.isOf(Items.BONE)) return handleBone(gh, stack, serverPlayer, paused);
         if (stack.isOf(Items.SNOWBALL)) return handleSnowball(gh, stack, serverPlayer, paused, player, hand);
         if (stack.isOf(Items.LEAD)) return handleLead(gh, stack, player, hand);
         if (saddled && !gh.hasPassengers()) { player.startRiding(gh); return ActionResult.SUCCESS; }
         if (stack.isOf(Items.SADDLE)) return handleSaddle(gh, stack, serverPlayer, player);

         // 成年生物额外交互：潜行+右键切换待命
         if (!gh.isBaby() && player.isSneaking()) {
            toggleStayingMode(gh, player);
         }

         return ActionResult.PASS;
      });
   }

   // 类型检查与条件
   private static boolean canProcess(net.minecraft.entity.player.PlayerEntity player,
                                     net.minecraft.world.World world,
                                     Hand hand,
                                     net.minecraft.entity.Entity entity) {
      if (world.isClient || hand != Hand.MAIN_HAND) return false;
      if (!(player instanceof ServerPlayerEntity)) return false;
      return entity instanceof HappyGhastEntity;
   }

   // 防抖处理
   private static boolean shouldProcessInteraction(UUID playerId, long tick) {
      Long last = LAST_INTERACT_TICK.get(playerId);
      if (last != null && tick - last < 2) return false;
      LAST_INTERACT_TICK.put(playerId, tick);
      return true;
   }

   private static ActionResult handleGoldenApple(HappyGhastEntity gh, ItemStack stack, ServerPlayerEntity player, boolean paused) {
      if (!paused) {
         gh.getDataTracker().set(HappyGhastData.AGE_PAUSED, true);
         decrementIfNotCreative(player, stack);
         Messages.sendMessage(player, Text.translatable("gp.growStop"), true);
      } else {
         Messages.sendMessage(player, Text.translatable("gp.growStopped"), true);
      }
      return ActionResult.SUCCESS;
   }

   private static ActionResult handleSugar(HappyGhastEntity gh, ItemStack stack, ServerPlayerEntity player, boolean paused) {
      if (paused) {
         gh.getDataTracker().set(HappyGhastData.AGE_PAUSED, false);
         decrementIfNotCreative(player, stack);
         Messages.sendMessage(player, Text.translatable("gp.unpaused"), true);
      } else {
         Messages.sendMessage(player, Text.translatable("gp.notPaused"), true);
      }
      return ActionResult.SUCCESS;
   }

   private static ActionResult handleBone(HappyGhastEntity gh, ItemStack stack, ServerPlayerEntity player, boolean paused) {
      if (!paused) {
         gh.growUp(-60, true);
         gh.getNavigation().stop();
         gh.getMoveControl().moveTo(gh.getX(), gh.getY(), gh.getZ(), 0.0D);
         decrementIfNotCreative(player, stack);
         Messages.sendMessage(player, Text.translatable("gp.ageReverted"), true);
      } else {
         Messages.sendMessage(player, Text.translatable("gp.cannotRevert"), true);
      }
      return ActionResult.SUCCESS;
   }

   private static ActionResult handleSnowball(HappyGhastEntity gh, ItemStack stack, ServerPlayerEntity player, boolean paused,
                                              net.minecraft.entity.player.PlayerEntity p, Hand hand) {
      if (paused) {
         Messages.sendMessage(player, Text.translatable("gp.snowballPaused"), true);
         return ActionResult.SUCCESS;
      }
      return gh.interact(p, hand);
   }

   private static ActionResult handleLead(HappyGhastEntity gh, ItemStack stack,
                                          net.minecraft.entity.player.PlayerEntity player, Hand hand) {
      int originalCount = stack.getCount();
      ActionResult result = gh.interact(player, hand);
      if (player.getAbilities().creativeMode) stack.setCount(originalCount);
      return result;
   }

   private static ActionResult handleSaddle(HappyGhastEntity gh, ItemStack stack, ServerPlayerEntity player,
                                            net.minecraft.entity.player.PlayerEntity p) {
      if (!gh.isBaby()) {
         Messages.sendMessage(player, Text.translatable("gp.onlyBabySaddle"), true);
         return ActionResult.PASS;
      }
      gh.getDataTracker().set(HappyGhastData.SADDLED, true);
      decrementIfNotCreative(player, stack);
      player.startRiding(gh);
      Messages.sendMessage(player, Text.translatable("gp.saddledAndRidden"), true);
      return ActionResult.SUCCESS;
   }

   private static void decrementIfNotCreative(net.minecraft.entity.player.PlayerEntity player, ItemStack stack) {
      if (!player.isCreative()) stack.decrement(1);
   }

   private static void toggleStayingMode(HappyGhastEntity gh, net.minecraft.entity.player.PlayerEntity player) {
      boolean current = gh.getDataTracker().get(HappyGhastData.STAYING);
      boolean next = !current;
      gh.getDataTracker().set(HappyGhastData.STAYING, next);

      // 同步原版 STAY 标记显示效果
      TrackedData<Boolean> STAY = top.diaoyugan.ghastling_plus.mixin.HappyGhastAccessor.gh_getStayingStill();
      gh.getDataTracker().set(STAY, next);

      // 给玩家发送提示
      if (player instanceof ServerPlayerEntity serverPlayer) {
         Messages.sendMessage(serverPlayer,
                 Text.translatable(next ? "gp.manualStayingOn" : "gp.manualStayingOff"), true);
      }

   }

}
