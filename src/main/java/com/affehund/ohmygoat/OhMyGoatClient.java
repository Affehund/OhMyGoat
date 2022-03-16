package com.affehund.ohmygoat;

import com.affehund.ohmygoat.client.layer.HornedHelmetLayer;
import com.affehund.ohmygoat.client.model.HornedHelmetModel;
import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.GoatTags;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OhMyGoatClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(OhMyGoatClient::getItemColorProvider, GoatRegistry.HORNED_LEATHER_HELMET);
        EntityModelLayerRegistry.registerModelLayer(HornedHelmetModel.HORNED_HELMET_LAYER, HornedHelmetModel::getTexturedModelData);
        ItemTooltipCallback.EVENT.register(OhMyGoatClient::getTooltip);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(OhMyGoatClient::registerRenderers);
    }

    private static int getItemColorProvider(ItemStack stack, int tintIndex) {
        return tintIndex != 0 ? -1 : ((DyeableArmorItem) stack.getItem()).getColor(stack);
    }

    private static void registerRenderers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {
        if (entityRenderer instanceof BipedEntityRenderer || entityRenderer instanceof ArmorStandEntityRenderer) {
            registrationHelper.register(new HornedHelmetLayer(entityRenderer, context.getModelLoader()));
        } else if (entityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
            registrationHelper.register(new HornedHelmetLayer<>(playerEntityRenderer, context.getModelLoader()));
        }
    }

    private static void getTooltip(ItemStack stack, TooltipContext context, List<Text> tooltips) {
        if (OhMyGoat.CONFIG.SHOW_TOOLTIPS && stack.isIn(GoatTags.Items.HORNED_HELMETS)) {
            var roundedProbability = Math.round(OhMyGoat.CONFIG.HURT_ATTACKER_PROBABILITY * 100 * 10.0) / 10.0;
            tooltips.add(new TranslatableText("tooltip.ohmygoat.horned_helmet_1", roundedProbability).formatted(Formatting.AQUA));

            var sneakKey = MinecraftClient.getInstance().options.sneakKey;
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), sneakKey.boundKey.getCode())) {
                tooltips.add(new TranslatableText("tooltip.ohmygoat.horned_helmet_2"));
                tooltips.add(new TranslatableText("tooltip.ohmygoat.horned_helmet_3"));
            } else {
                tooltips.add(new TranslatableText("tooltip.ohmygoat.hold_shift", Formatting.YELLOW + sneakKey.getBoundKeyLocalizedText().getString()));
            }
        }
    }
}
