package top.diaoyugan.ghastling_plus.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

public class Ghastling_plusDataGenerator implements DataGeneratorEntrypoint {
   public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
      Pack pack = fabricDataGenerator.createPack();
   }
}
