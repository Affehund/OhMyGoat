package com.affehund.ohmygoat.mixin;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.GoatTags;
import com.affehund.ohmygoat.core.GoatUtilities;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.ItemScatterer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var livingEntity = (LivingEntity) (Object) this;
        var random = livingEntity.getRandom();
        var world = livingEntity.world;

        if (source instanceof EntityDamageSource && source.getSource() instanceof LivingEntity attacker) {

            for (ItemStack stack : livingEntity.getArmorItems()) {

                if (stack.isIn(GoatTags.Items.HORNED_HELMETS) && !GoatUtilities.hasEnchantment(stack, Enchantments.THORNS)) {

                    // probability to hurt attacker
                    if (random.nextDouble() < OhMyGoat.CONFIG.HURT_ATTACKER_PROBABILITY) {

                        attacker.damage(DamageSource.thorns(livingEntity), amount > 10 ? amount - 10 : 1 + random.nextInt(4));

                        var equipmentSlot = LivingEntity.getPreferredEquipmentSlot(stack);
                        stack.damage(2, livingEntity, (consumer) -> consumer.sendEquipmentBreakStatus(equipmentSlot));

                        // probability to lose horns
                        if (random.nextDouble() < OhMyGoat.CONFIG.HORN_LOSING_PROBABILITY) {

                            var goatHorn = new ItemStack(GoatRegistry.GOAT_HORN);
                            var smithingRecipes = world.getRecipeManager().listAllOfType(RecipeType.SMITHING);
                            var optionalRecipe = smithingRecipes.stream().filter(recipe2 -> recipe2.getOutput().getItem().equals(stack.getItem()) && recipe2.testAddition(goatHorn)).findFirst();

                            optionalRecipe.ifPresent(upgradeRecipe -> {
                                var hornlessHelmet = upgradeRecipe.base.getMatchingStacks()[0].copy();

                                if (hornlessHelmet.isIn(GoatTags.Items.HORNABLE_HELMETS)) {
                                    NbtCompound resultNbt = stack.getNbt();
                                    if (resultNbt != null) hornlessHelmet.setNbt(resultNbt.copy());

                                    livingEntity.getStackReference(100 + equipmentSlot.getEntitySlotId()).set(hornlessHelmet);

                                    // probability to drop horn
                                    if (random.nextDouble() < OhMyGoat.CONFIG.HORN_DROPPING_PROBABILITY) {
                                        var pos = livingEntity.getBlockPos();
                                        int damage = (int) (54.0 * stack.getDamage() / stack.getMaxDamage()) + GoatUtilities.randomInRange(world.getRandom(), 1, 5);

                                        goatHorn.setDamage(damage + GoatUtilities.randomInRange(world.getRandom(), 1, 5));
                                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), goatHorn);
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
