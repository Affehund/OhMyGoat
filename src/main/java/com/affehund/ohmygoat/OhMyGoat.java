package com.affehund.ohmygoat;

import com.affehund.ohmygoat.core.GoatDataGenerator;
import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.compat.top.CheeseMakingProbeInfoProvider;
import com.affehund.ohmygoat.core.util.GoatConfig;
import com.affehund.ohmygoat.core.util.GoatTags;
import com.affehund.ohmygoat.core.util.GoatUtilities;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OhMyGoat.MOD_ID)
public class OhMyGoat {
    public static final String MOD_ID = "ohmygoat";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public OhMyGoat() {
        LOGGER.debug("Loading up {}!", MOD_ID);
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        var forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMCMessage);
        modEventBus.addListener(this::gatherData);

        GoatRegistry.BLOCKS.register(modEventBus);
        GoatRegistry.BLOCK_ENTITIES.register(modEventBus);
        GoatRegistry.ITEMS.register(modEventBus);
        GoatRegistry.POTIONS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GoatConfig.COMMON_SPEC);

        forgeEventBus.register(this);
        LOGGER.debug("{} has finished loading for now!", MOD_ID);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GoatRegistry.registerBrewingRecipes();
            GoatRegistry.registerCauldronInteractions();
            GoatRegistry.registerCriteriaTriggers();
        });
    }

    private void enqueueIMCMessage(InterModEnqueueEvent event) {
        var TOP = "theoneprobe";
        if (GoatUtilities.isModLoaded(TOP)) {
            InterModComms.sendTo(TOP, "getTheOneProbe", CheeseMakingProbeInfoProvider::new);
        }
    }

    private void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(new GoatDataGenerator.BlockStateGenerator(generator, MOD_ID, existingFileHelper));
            generator.addProvider(new GoatDataGenerator.ItemModelGenerator(generator, MOD_ID, existingFileHelper));
            generator.addProvider(new GoatDataGenerator.LanguageGenerator(generator, MOD_ID));
        }

        if (event.includeServer()) {
            generator.addProvider(new GoatDataGenerator.AdvancementGenerator(generator, existingFileHelper));
            var blockTagsProvider = new GoatDataGenerator.BlockTagsGenerator(generator, MOD_ID, existingFileHelper);
            generator.addProvider(blockTagsProvider);
            generator.addProvider(new GoatDataGenerator.ItemTagsGenerator(generator, blockTagsProvider, MOD_ID, existingFileHelper));
            generator.addProvider(new GoatDataGenerator.LootTableGenerator(generator));
            generator.addProvider(new GoatDataGenerator.RecipeGenerator(generator));
        }
    }

    @SubscribeEvent
    public void loadLootTables(LootTableLoadEvent event) {
        if (GoatConfig.ADD_OUTPOST_LOOT.get()) {
            var name = event.getName();
            if (name.equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
                LOGGER.debug("Injecting loot table {} from {}", name, OhMyGoat.MOD_ID);
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootTableReference.lootTableReference(new ResourceLocation(MOD_ID, "inject/chests/pillager_outpost")))
                        .name(MOD_ID + "_injection").build());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {

        var stack = event.getItemStack();
        var tooltips = event.getToolTip();

        if (stack.is(GoatTags.Items.HORNED_HELMETS) && GoatConfig.SHOW_TOOLTIPS.get()) {

            var roundedProbability = Math.round(GoatConfig.HURT_ATTACKER_PROBABILITY.get() * 100 * 10.0) / 10.0;
            tooltips.add(new TranslatableComponent("tooltip.ohmygoat.horned_helmet_1", roundedProbability));

            var shiftKey = Minecraft.getInstance().options.keyShift;
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), shiftKey.getKey().getValue())) {
                tooltips.add(new TranslatableComponent("tooltip.ohmygoat.horned_helmet_2"));
                tooltips.add(new TranslatableComponent("tooltip.ohmygoat.horned_helmet_3"));
            } else {
                tooltips.add(new TranslatableComponent("tooltip.ohmygoat.hold_shift", ChatFormatting.YELLOW + shiftKey.getTranslatedKeyMessage().getString()));
            }
        }
    }
}
