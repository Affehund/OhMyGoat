package com.affehund.ohmygoat.common.block.entity;

import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.config.GoatConfig;
import com.affehund.ohmygoat.core.util.GoatUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GoatMilkCauldronBlockEntity extends BlockEntity {
    private int cookingProgress = 0;
    private int cookingTotalTime = GoatConfig.CHEESE_MAKING_DURATION.get();

    public GoatMilkCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, GoatMilkCauldronBlockEntity blockEntity) {
        boolean flag = false;

        if (state.getValue(LayeredCauldronBlock.LEVEL) > 0) {
            flag = true;
            blockEntity.cookingProgress++;
            if (blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                var min = GoatConfig.MIN_GOAT_CHEESE.get();
                var max = GoatConfig.MAX_GOAT_CHEESE.get();
                ItemStack itemstack = new ItemStack(GoatRegistry.GOAT_CHEESE.get(), GoatUtilities.randomInRange(level.getRandom(), min, max));
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.sendBlockUpdated(pos, state, state, 3);
                blockEntity.cookingProgress = 0;
            }
        }

        if (flag) {
            setChanged(level, pos, state);
        }
    }

    public float getCookingProgress() {
        return cookingTotalTime != 0 ? (float) this.cookingProgress / (float) cookingTotalTime : 0.0F;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.cookingProgress = tag.getInt("CookingTime");
        this.cookingTotalTime = tag.getInt("CookingTotalTime");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("CookingTime", this.cookingProgress);
        tag.putInt("CookingTotalTime", this.cookingTotalTime);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
