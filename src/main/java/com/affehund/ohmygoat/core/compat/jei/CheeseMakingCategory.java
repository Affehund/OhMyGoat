package com.affehund.ohmygoat.core.compat.jei;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.util.GoatConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class CheeseMakingCategory implements IRecipeCategory<CheeseMakingWrapper> {

    public static final RecipeType<CheeseMakingWrapper> TYPE = RecipeType.create(OhMyGoat.MOD_ID, "cheese_making", CheeseMakingWrapper.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(OhMyGoat.MOD_ID, "textures/gui/cheese_making.png");

    private final IDrawable icon;
    private final IDrawableStatic background;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public CheeseMakingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 81, 40);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Items.CAULDRON));
        this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<>() {
            @Override
            public @NotNull IDrawableAnimated load(@NotNull Integer cookingTime) {
                return guiHelper.drawableBuilder(TEXTURE, 82, 0, 24, 17)
                        .buildAnimated(cookingTime, IDrawableAnimated.StartDirection.LEFT, false);
            }
        });
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull Class<? extends CheeseMakingWrapper> getRecipeClass() {
        return TYPE.getRecipeClass();
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull ResourceLocation getUid() {
        return TYPE.getUid();
    }

    @Override
    public @NotNull RecipeType<CheeseMakingWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return new TranslatableComponent("category.ohmygoat.cheese_making");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }


    @Override
    public void draw(@NotNull CheeseMakingWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack poseStack, double mouseX, double mouseY) {
        this.getArrow(recipe).draw(poseStack, 24, 5);
        this.drawCookTime(recipe, poseStack);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CheeseMakingWrapper recipe, @NotNull IFocusGroup focuses) {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStack(recipe.input());
        IRecipeSlotBuilder result = builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 5).addItemStack(recipe.result());
        builder.createFocusLink(input, result);
    }

    private IDrawableAnimated getArrow(CheeseMakingWrapper recipe) {
        int cookingTime = recipe.cookingTime();
        if (cookingTime <= 0) {
            cookingTime = GoatConfig.CHEESE_MAKING_DURATION.get();
        }
        return this.cachedArrows.getUnchecked(cookingTime);
    }

    private void drawCookTime(CheeseMakingWrapper recipe, PoseStack poseStack) {
        int cookTime = recipe.cookingTime();
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            fontRenderer.draw(poseStack, timeString, background.getWidth() - stringWidth, 31, 0xFF808080);
        }
    }
}
