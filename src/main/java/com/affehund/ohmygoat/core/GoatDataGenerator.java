package com.affehund.ohmygoat.core;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.util.GoatTags;
import com.affehund.ohmygoat.core.util.SmithItemTrigger;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GoatDataGenerator {

    /*
     client-side data generators
    */

    public static class BlockStateGenerator extends BlockStateProvider {

        public BlockStateGenerator(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
            super(generator, modId, existingFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            cauldronBlock(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK.get(), new ResourceLocation(OhMyGoat.MOD_ID, "block/goat_milk_still"));
        }

        private void cauldronBlock(Block block, ResourceLocation content) {
            var cauldron1 = this.models().getBuilder(Objects.requireNonNull(block.getRegistryName()).getPath() + "_level1")
                    .parent(this.models().getExistingFile(mcLoc("block/template_cauldron_level1")))
                    .texture("content", content)
                    .texture("inside", this.mcLoc("block/cauldron_inner"))
                    .texture("particle", this.mcLoc("block/cauldron_side"))
                    .texture("top", this.mcLoc("block/cauldron_top"))
                    .texture("bottom", this.mcLoc("block/cauldron_bottom"))
                    .texture("side", this.mcLoc("block/cauldron_side"));

            var cauldron2 = this.models().getBuilder(Objects.requireNonNull(block.getRegistryName()).getPath() + "_level2")
                    .parent(this.models().getExistingFile(mcLoc("block/template_cauldron_level2")))
                    .texture("content", content)
                    .texture("inside", this.mcLoc("block/cauldron_inner"))
                    .texture("particle", this.mcLoc("block/cauldron_side"))
                    .texture("top", this.mcLoc("block/cauldron_top"))
                    .texture("bottom", this.mcLoc("block/cauldron_bottom"))
                    .texture("side", this.mcLoc("block/cauldron_side"));

            var cauldronFull = this.models().getBuilder(Objects.requireNonNull(block.getRegistryName()).getPath() + "_full")
                    .parent(this.models().getExistingFile(this.mcLoc("block/template_cauldron_full")))
                    .texture("content", content)
                    .texture("inside", this.mcLoc("block/cauldron_inner"))
                    .texture("particle", this.mcLoc("block/cauldron_side"))
                    .texture("top", this.mcLoc("block/cauldron_top"))
                    .texture("bottom", this.mcLoc("block/cauldron_bottom"))
                    .texture("side", this.mcLoc("block/cauldron_side"));

            this.getVariantBuilder(block).forAllStates(state -> {
                switch (state.getValue(LayeredCauldronBlock.LEVEL)) {
                    case 1:
                        return ConfiguredModel.builder().modelFile(cauldron1).build();
                    case 2:
                        return ConfiguredModel.builder().modelFile(cauldron2).build();
                    case 3:
                        return ConfiguredModel.builder().modelFile(cauldronFull).build();
                    default:
                }
                return new ConfiguredModel[0];
            });
        }
    }

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
            super(generator, modId, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            for (ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item != null && OhMyGoat.MOD_ID.equals(id.getNamespace())) {
                    if (item instanceof BlockItem) {
                        defaultBlock(id, (BlockItem) item);
                    } else if (this.getHelmetItem(item) != null) {
                        hornedHelmetItem(id, item);
                    } else {
                        defaultItem(id, item);
                    }
                }
            }
        }

        private void defaultBlock(ResourceLocation id, BlockItem item) {
            this.getBuilder(id.getPath()).parent(
                    new ModelFile.UncheckedModelFile(new ResourceLocation(id.getNamespace(), "block/" + id.getPath())));
            OhMyGoat.LOGGER.debug("Generated default block item model for: {}", item.getRegistryName());
        }

        private void defaultItem(ResourceLocation id, Item item) {
            this.withExistingParent(id.getPath(), "item/generated")
                    .texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
            OhMyGoat.LOGGER.debug("Generated default item model for: {}", item.getRegistryName());
        }

        private void hornedHelmetItem(ResourceLocation id, Item item) {
            if (item.equals(GoatRegistry.HORNED_LEATHER_HELMET.get())) {
                this.withExistingParent(id.getPath(), "item/generated")
                        .texture("layer0", new ResourceLocation("item/" + this.getHelmetItem(item)))
                        .texture("layer1", new ResourceLocation(id.getNamespace(), "item/horned_helmet_overlay"))
                        .texture("layer2", new ResourceLocation("item/" + this.getHelmetItem(item) + "_overlay"));
            } else {
                this.withExistingParent(id.getPath(), "item/generated")
                        .texture("layer0", new ResourceLocation("item/" + this.getHelmetItem(item)))
                        .texture("layer1", new ResourceLocation(id.getNamespace(), "item/horned_helmet_overlay"));
            }
            OhMyGoat.LOGGER.debug("Generated horned helmet item model for: {}", item.getRegistryName());
        }

        private Item getHelmetItem(Item item) {
            var itemName = Objects.requireNonNull(item.getRegistryName()).getPath();
            if (itemName.startsWith("horned_")) {
                var helmetName = itemName.replace("horned_", "");
                return ForgeRegistries.ITEMS.getValue(new ResourceLocation(helmetName));
            }
            return null;
        }
    }

    public static class LanguageGenerator extends LanguageProvider {

        public LanguageGenerator(DataGenerator gen, String modId) {
            super(gen, modId, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("_comment", "Translation (en_us) by Affehund");
            add(GoatRegistry.OH_MY_GOAT_TAB.getDisplayName().getString(), "Oh My Goat");

            ForgeRegistries.ITEMS.getValues()
                    .stream().filter(i -> (i != null && Objects.requireNonNull(i.getRegistryName()).getNamespace().equals(OhMyGoat.MOD_ID)))
                    .forEach(item -> {
                        String name = StringUtils.capitaliseAllWords(item.getRegistryName().getPath().replace("_", " "));
                        add(item, name);
                    });

            add("category.ohmygoat.cheese_making", "Cheese Making");

            addAdvancement("spyglass_at_goat", "Is It a fluffy Bed?", "Look at a goat through a spyglass");
            addAdvancement("rammed_by_goat", "I Goat It", "Get rammed by a goat");
            addAdvancement("use_goat_horn", "Oh my Goat!", "Use a goat horn to scare everyone");
            addAdvancement("cheese_maker", "Cheese Maker", "Make goat cheese by placing goat milk into a cauldron");
            addAdvancement("horned_helmet", "Horned Helmet", "Craft a horned helmet by combining any helmet with a goat horn in the smithing table.");

            addGoatScreamPotion();

            addTooltip("goat_horn", "Hold %s to scare others.");
            addTooltip("horned_helmet_1", "Can hurt others with a probability of %s percent.");
            addTooltip("horned_helmet_2", "But you can lose those horns, too.");
            addTooltip("horned_helmet_3", "Therefore you should use it wisely!");
            addTooltip("hold_shift", "Hold %s for more information.");
        }

        private void addAdvancement(String key, String title, String description) {
            add("ohmygoat.advancements.ohmygoat." + key + ".title", title);
            add("ohmygoat.advancements.ohmygoat." + key + ".description", description);
        }

        private void addGoatScreamPotion() {
            String key = Objects.requireNonNull(((Supplier<? extends Potion>) GoatRegistry.GOAT_SCREAM_POTION).get().getRegistryName()).getPath();
            add("item.minecraft.potion.effect." + key, "Potion of the " + "Goat Scream");
            add("item.minecraft.splash_potion.effect." + key, "Splash Potion of the " + "Goat Scream");
            add("item.minecraft.lingering_potion.effect." + key, "Lingering Potion of the " + "Goat Scream");
            add("item.minecraft.tipped_arrow.effect." + key, "Arrow of the " + "Goat Scream");
        }

        private void addTooltip(String key, String translation) {
            add("tooltip.ohmygoat." + key, translation);
        }
    }


    /*
     server-side data generators
    */

    public static class AdvancementGenerator extends AdvancementProvider {

        public AdvancementGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, existingFileHelper);
        }

        @Override
        protected void registerAdvancements(@NotNull Consumer<Advancement> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            var spyglassOnGoat = Advancement.Builder.advancement().parent(new ResourceLocation("adventure/root"))
                    .display(Items.SPYGLASS,
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.spyglass_at_goat.title"),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.spyglass_at_goat.description"),
                            null, FrameType.TASK, true, true, false)
                    .addCriterion("spyglass_at_goat", lookAtThroughItem())
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, "adventure/spyglass_at_goat"), existingFileHelper);

            var rammedByGoat = Advancement.Builder.advancement().parent(spyglassOnGoat)
                    .display(Items.ARROW,
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.rammed_by_goat.title"),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.rammed_by_goat.description"),
                            null, FrameType.TASK, true, true, false)
                    .addCriterion("rammed_by_goat", entityHurtPlayer())
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, "adventure/rammed_by_goat"), existingFileHelper);

            Advancement.Builder.advancement().parent(rammedByGoat)
                    .display(GoatRegistry.GOAT_HORN.get(),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.use_goat_horn.title"),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.use_goat_horn.description"),
                            null, FrameType.TASK, true, true, false)
                    .addCriterion("use_goat_horn", changedDurability(GoatRegistry.GOAT_HORN.get()))
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, "adventure/use_goat_horn"), existingFileHelper);

            Advancement.Builder.advancement().parent(spyglassOnGoat)
                    .display(GoatRegistry.GOAT_MILK_BUCKET.get(),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.cheese_maker.title"),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.cheese_maker.description"),
                            null, FrameType.TASK, true, true, false)
                    .addCriterion("cheese_maker", useOnBlock(GoatRegistry.GOAT_MILK_BUCKET.get()))
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, "adventure/cheese_maker"), existingFileHelper);

            Advancement.Builder.advancement().parent(rammedByGoat)
                    .display(GoatRegistry.HORNED_NETHERITE_HELMET.get(),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.horned_helmet.title"),
                            new TranslatableComponent("ohmygoat.advancements.ohmygoat.horned_helmet.description"),
                            null, FrameType.TASK, true, true, false)
                    .addCriterion("horned_helmet", upgradeHelmet(GoatRegistry.GOAT_HORN.get()))
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, "adventure/horned_helmet"), existingFileHelper);
        }

        private static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemLike item) {
            return ItemDurabilityTrigger.TriggerInstance.changedDurability(ItemPredicate.Builder.item().of(item).build(), MinMaxBounds.Ints.ANY);
        }

        private static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer() {
            return EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(DamagePredicate.Builder.damageInstance()
                    .sourceEntity(EntityPredicate.Builder.entity().of(EntityType.GOAT).build()).build());
        }

        private static UsingItemTrigger.TriggerInstance lookAtThroughItem() {
            return UsingItemTrigger.TriggerInstance.lookingAt(EntityPredicate.Builder.entity().player(PlayerPredicate.Builder.player()
                    .setLookingAt(EntityPredicate.Builder.entity().of(EntityType.GOAT).build()).build()), ItemPredicate.Builder.item().of(Items.SPYGLASS));
        }

        private static SmithItemTrigger.TriggerInstance upgradeHelmet(ItemLike upgradeItem) {
            return SmithItemTrigger.TriggerInstance.smithItem(ItemPredicate.Builder.item(), ItemPredicate.Builder.item().of(upgradeItem), ItemPredicate.Builder.item());
        }

        private static ItemUsedOnBlockTrigger.TriggerInstance useOnBlock(ItemLike item) {
            return ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(GoatTags.Blocks.CAULDRONS).build()), ItemPredicate.Builder.item().of(item));
        }
    }

    public static class BlockTagsGenerator extends BlockTagsProvider {

        public BlockTagsGenerator(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(GoatTags.Blocks.CAULDRONS).add(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK.get());
            tag(GoatTags.Blocks.MINEABLE_PICKAXE).add(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK.get());
        }
    }

    public static class ItemTagsGenerator extends ItemTagsProvider {

        public ItemTagsGenerator(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, blockTagsProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(GoatTags.Items.HORNABLE_HELMETS).add(Items.CHAINMAIL_HELMET).add(Items.DIAMOND_HELMET).add(Items.GOLDEN_HELMET).add(Items.IRON_HELMET).add(Items.LEATHER_HELMET).add(Items.NETHERITE_HELMET);
            tag(GoatTags.Items.HORNED_HELMETS).add(GoatRegistry.HORNED_CHAINMAIL_HELMET.get()).add(GoatRegistry.HORNED_DIAMOND_HELMET.get()).add(GoatRegistry.HORNED_GOLDEN_HELMET.get()).add(GoatRegistry.HORNED_IRON_HELMET.get()).add(GoatRegistry.HORNED_LEATHER_HELMET.get()).add(GoatRegistry.HORNED_NETHERITE_HELMET.get());
            tag(GoatTags.Items.PIGLIN_LOVED).add(GoatRegistry.HORNED_GOLDEN_HELMET.get());
        }
    }

    public static class LootTableGenerator extends LootTableProvider {

        public LootTableGenerator(DataGenerator generator) {
            super(generator);
        }

        @Override
        protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return ImmutableList.of(
                    Pair.of(Blocks::new, LootContextParamSets.BLOCK),
                    Pair.of(Entities::new, LootContextParamSets.ENTITY)
            );
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationTracker) {
            map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationTracker, resourceLocation, lootTable));
        }

        public static class Blocks extends BlockLoot {
            @Override
            protected void addTables() {
                this.dropOther(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK.get(), Items.CAULDRON);
            }

            @Override
            protected @NotNull Iterable<Block> getKnownBlocks() {
                return Collections.singletonList(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK.get());
            }
        }

        public static class Entities extends EntityLoot {
            @Override
            protected void addTables() {
                this.add(EntityType.GOAT, createGoatTable());
            }

            private LootTable.Builder createGoatTable() {
                LootPool.Builder chevonPool = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(GoatRegistry.CHEVON.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))));
                LootPool.Builder goatHornPool = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(GoatRegistry.GOAT_HORN.get())).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025F, 0.01F));
                return LootTable.lootTable().withPool(chevonPool)/*.withPool(furPool)*/.withPool(goatHornPool);
            }

            @Override
            protected @NotNull Iterable<EntityType<?>> getKnownEntities() {
                return Collections.singletonList(EntityType.GOAT);
            }
        }
    }

    public static class RecipeGenerator extends RecipeProvider {

        public RecipeGenerator(DataGenerator generator) {
            super(generator);
        }

        @Override
        protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
            buildCookingRecipe(consumer, GoatRegistry.CHEVON.get(), GoatRegistry.COOKED_CHEVON.get());

            buildSmithingRecipe(consumer, Items.CHAINMAIL_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_CHAINMAIL_HELMET.get());
            buildSmithingRecipe(consumer, Items.DIAMOND_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_DIAMOND_HELMET.get());
            buildSmithingRecipe(consumer, Items.GOLDEN_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_GOLDEN_HELMET.get());
            buildSmithingRecipe(consumer, Items.IRON_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_IRON_HELMET.get());
            buildSmithingRecipe(consumer, Items.LEATHER_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_LEATHER_HELMET.get());
            buildSmithingRecipe(consumer, Items.NETHERITE_HELMET, GoatRegistry.GOAT_HORN.get(), GoatRegistry.HORNED_NETHERITE_HELMET.get());
        }

        private static void buildCookingRecipe(Consumer<FinishedRecipe> consumer, Item input, Item result) {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), result, (float) 0.4, 200)
                    .unlockedBy("has_" + input, has(input)).save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, input + "_from_furnace"));
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), result, (float) 0.4, 100)
                    .unlockedBy("has_" + input, has(input)).save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, input + "_from_smoking"));
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), result, (float) 0.4, 600)
                    .unlockedBy("has_" + input, has(input)).save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, input + "_from_campfire_cooking"));
        }

        private static void buildSmithingRecipe(Consumer<FinishedRecipe> consumer, ItemLike inputItem, ItemLike upgradeItem, Item result) {
            UpgradeRecipeBuilder.smithing(Ingredient.of(inputItem), Ingredient.of(upgradeItem), result)
                    .unlocks("has_" + inputItem.toString(), has(inputItem))
                    .unlocks("has_" + upgradeItem.toString(), has(upgradeItem))
                    .save(consumer, new ResourceLocation(OhMyGoat.MOD_ID, result + "_from_smithing"));
        }
    }
}
