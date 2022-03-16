package com.affehund.ohmygoat.common.block.entity;

import com.affehund.ohmygoat.OhMyGoat;
import com.affehund.ohmygoat.core.GoatRegistry;
import com.affehund.ohmygoat.core.GoatUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GoatMilkCauldronBlockEntity extends BlockEntity {
    private int cookingProgress = 0;
    private int cookingTotalTime = OhMyGoat.CONFIG.CHEESE_MAKING_DURATION;

    public GoatMilkCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(GoatRegistry.GOAT_MILK_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    public static void cookTick(World world, BlockPos pos, BlockState state, GoatMilkCauldronBlockEntity blockEntity) {
        boolean flag = false;

        if (state.get(LeveledCauldronBlock.LEVEL) > 0) {
            flag = true;
            blockEntity.cookingProgress++;
            if (blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                var min = OhMyGoat.CONFIG.MIN_GOAT_CHEESE;
                var max = OhMyGoat.CONFIG.MAX_GOAT_CHEESE;
                ItemStack itemstack = new ItemStack(GoatRegistry.GOAT_CHEESE, GoatUtilities.randomInRange(world.getRandom(), min, max));
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                blockEntity.cookingProgress = 0;
            }
        }

        if (flag) {
            markDirty(world, pos, state);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.cookingProgress = nbt.getInt("CookingTime");
        this.cookingTotalTime = nbt.getInt("CookingTotalTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("CookingTime", this.cookingProgress);
        nbt.putInt("CookingTotalTime", this.cookingTotalTime);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
