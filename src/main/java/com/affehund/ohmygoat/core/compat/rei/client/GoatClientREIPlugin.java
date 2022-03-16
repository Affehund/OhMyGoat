package com.affehund.ohmygoat.core.compat.rei.client;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.compat.rei.CheeseMakingDisplay;
import com.affehund.ohmygoat.core.compat.rei.GoatREIPlugin;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoatClientREIPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CheeseMakingCategory());
        registry.addWorkstations(GoatREIPlugin.CHEESE_MAKING, EntryStacks.of(Items.CAULDRON));
        registry.removePlusButton(GoatREIPlugin.CHEESE_MAKING);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerDisplayGenerator(GoatREIPlugin.CHEESE_MAKING, new DynamicDisplayGenerator<CheeseMakingDisplay>() {
            @Override
            public Optional<List<CheeseMakingDisplay>> getRecipeFor(EntryStack<?> entry) {
                if (entry.getType() == VanillaEntryTypes.ITEM) {
                    var item = ((ItemStack) entry.getValue()).getItem();
                    if (item.equals(GoatRegistry.GOAT_CHEESE)) {
                        var list = Collections.singleton(new CheeseMakingDisplay(new ItemStack(GoatRegistry.GOAT_MILK_BUCKET), new ItemStack(GoatRegistry.GOAT_CHEESE), OhMyGoat.CONFIG.CHEESE_MAKING_DURATION));
                        return Optional.of(list.stream().toList());
                    }
                }
                return Optional.empty();
            }

            @Override
            public Optional<List<CheeseMakingDisplay>> getUsageFor(EntryStack<?> entry) {
                if (entry.getType() == VanillaEntryTypes.ITEM) {
                    var item = ((ItemStack) entry.getValue()).getItem();
                    if (item.equals(GoatRegistry.GOAT_MILK_BUCKET) || item.equals(Items.CAULDRON)) {
                        var list = Collections.singleton(new CheeseMakingDisplay(new ItemStack(GoatRegistry.GOAT_MILK_BUCKET), new ItemStack(GoatRegistry.GOAT_CHEESE), OhMyGoat.CONFIG.CHEESE_MAKING_DURATION));
                        return Optional.of(list.stream().toList());
                    }
                }
                return Optional.empty();
            }
        });
    }
}
