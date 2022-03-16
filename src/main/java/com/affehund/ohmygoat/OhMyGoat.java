package com.affehund.ohmygoat;

import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.config.GoatConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OhMyGoat implements ModInitializer {
    public static final String MOD_ID = "ohmygoat";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static GoatConfig CONFIG;

    @Override
    public void onInitialize() {
        LOGGER.debug("Loading up {}!", MOD_ID);

        GoatRegistry.registerBlocks();
        GoatRegistry.registerBlockEntities();
        GoatRegistry.registerCauldronBehaviours();
        GoatRegistry.registerCriteria();
        GoatRegistry.registerItems();
        GoatRegistry.registerPotions();

        AutoConfig.register(GoatConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(GoatConfig.class).getConfig();

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (id.equals(LootTables.PILLAGER_OUTPOST_CHEST) && CONFIG.ADD_OUTPOST_LOOT) {
                LootTable table = lootManager.getTable(new Identifier(MOD_ID, "inject/chests/pillager_outpost"));
                supplier.withPools(((FabricLootSupplier) table).getPools());
            }
        });

        LOGGER.debug("{} has finished loading for now!", MOD_ID);
    }
}
