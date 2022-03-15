package com.affehund.ohmygoat.core.compat.jei;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.GoatRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@JeiPlugin
public class GoatJeiPlugin implements IModPlugin {
    public static final String JEI = "jei";

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(OhMyGoat.MOD_ID, JEI);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.CAULDRON), CheeseMakingCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var goatCheeseRecipe = new CheeseMakingWrapper(new ItemStack(GoatRegistry.GOAT_MILK_BUCKET.get()), new ItemStack(GoatRegistry.GOAT_CHEESE.get()), 600);
        registration.addRecipes(CheeseMakingCategory.TYPE, Collections.singletonList(goatCheeseRecipe));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CheeseMakingCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
