package top.diaoyugan.ghastling_plus;

import net.fabricmc.api.ModInitializer;

public class Ghastling_plus implements ModInitializer {
   public void onInitialize() {
      HappyGhastData.init();
      HappyGhastControl.init();
   }
}
