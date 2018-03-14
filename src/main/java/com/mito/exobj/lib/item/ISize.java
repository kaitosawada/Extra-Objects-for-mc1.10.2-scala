package com.mito.exobj.lib.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISize {

	default int getSize(ItemStack itemstack) {
		int ret = 1;
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("size")) {
			ret = itemstack.getTagCompound().getInteger("size");
		}
		return ret;
	}

	default ItemStack setSize(ItemStack itemstack, int i) {
		NBTTagCompound nbt = itemstack.getItem().getNBTShareTag(itemstack);
		if (nbt != null) {
			nbt.setInteger("size", i);
		}
		return itemstack;
	}

	default double getRealSize(ItemStack itemstack) {
		return convToDoubleSize(getSize(itemstack));
	}

	default double convToDoubleSize(int isize) {
		return (double) isize * 0.05;
	}
}
