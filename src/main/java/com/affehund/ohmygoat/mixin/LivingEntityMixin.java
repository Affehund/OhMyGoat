package com.affehund.ohmygoat.mixin;

import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.util.GoatConfig;
import com.affehund.ohmygoat.core.util.GoatTags;
import com.affehund.ohmygoat.core.util.GoatUtilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"))
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var livingEntity = (LivingEntity) (Object) this;
        var random = livingEntity.getRandom();
        var level = livingEntity.getLevel();

        if (source instanceof EntityDamageSource && source.getEntity() instanceof LivingEntity attacker) {

            for (ItemStack stack : livingEntity.getArmorSlots()) {

                if (stack.is(GoatTags.Items.HORNED_HELMETS) && !GoatUtilities.hasEnchantment(stack, Enchantments.THORNS)) {

                    // probability to hurt attacker
                    if (random.nextDouble() < GoatConfig.HURT_ATTACKER_PROBABILITY.get()) {

                        attacker.hurt(DamageSource.thorns(livingEntity), amount > 10 ? amount - 10 : 1 + random.nextInt(4));

                        var equipmentSlot = LivingEntity.getEquipmentSlotForItem(stack);
                        stack.hurtAndBreak(2, livingEntity, (consumer) -> consumer.broadcastBreakEvent(equipmentSlot));

                        // probability to lose horns
                        if (random.nextDouble() < GoatConfig.HORN_LOSING_PROBABILITY.get()) {

                            var goatHorn = new ItemStack(GoatRegistry.GOAT_HORN.get());
                            var smithingRecipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
                            var optionalRecipe = smithingRecipes.stream().filter(recipe2 -> recipe2.getResultItem().getItem().equals(stack.getItem()) && recipe2.isAdditionIngredient(goatHorn)).findFirst();

                            optionalRecipe.ifPresent(upgradeRecipe -> {
                                var hornlessHelmet = upgradeRecipe.base.getItems()[0].copy();

                                if (hornlessHelmet.is(GoatTags.Items.HORNABLE_HELMETS)) {
                                    CompoundTag resultCompoundTag = stack.getTag();
                                    if (resultCompoundTag != null) hornlessHelmet.setTag(resultCompoundTag.copy());

                                    livingEntity.getSlot(100 + equipmentSlot.getIndex()).set(hornlessHelmet);

                                    // probability to drop horn
                                    if (random.nextDouble() < GoatConfig.HORN_DROPPING_PROBABILITY.get()) {
                                        var pos = livingEntity.blockPosition();
                                        int damage = (int) (54.0 * stack.getDamageValue() / stack.getMaxDamage()) + GoatUtilities.randomInRange(level.getRandom(), 1, 5);

                                        goatHorn.setDamageValue(damage + GoatUtilities.randomInRange(level.getRandom(), 1, 5));
                                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), goatHorn);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
