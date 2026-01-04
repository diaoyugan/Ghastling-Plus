package top.diaoyugan.ghastling_plus;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Messages {
   @Environment(EnvType.CLIENT)
   public static void clientMessage(Text message, Boolean isOnActionbar) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (isOnActionbar) {
         client.inGameHud.setOverlayMessage(message, false);
      } else {
         client.inGameHud.getChatHud().addMessage(message);
      }
   }

   public static void sendMessage(ServerPlayerEntity player, Text message, Boolean isOnActionbar) {
      if (isOnActionbar) {
         Objects.requireNonNull(player.getEntityWorld().getServer()).execute(() -> player.sendMessage(message, true));
      } else {
         Objects.requireNonNull(player.getEntityWorld().getServer()).execute(() -> player.sendMessage(message, false));
      }
   }

   public static void sendTitleMessage(ServerPlayerEntity player, Text title) {
      player.networkHandler.sendPacket(new TitleS2CPacket(title));
   }

   public static void sendTitleMessage(ServerPlayerEntity player, Text title, Text subtitle) {
      player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
      player.networkHandler.sendPacket(new TitleS2CPacket(title));
   }
}
