package com.mito.exobj.lib.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IMaterial {

	default Block getMaterial(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			if (itemstack.getTagCompound().hasKey("material")) {
				Block b = Block.getBlockById(itemstack.getTagCompound().getInteger("material"));
				return b;
			}
		}
		return Blocks.WOOL;
	}

	default ItemStack setMaterial(ItemStack itemstack, Block e) {
		NBTTagCompound nbt = itemstack.getItem().getNBTShareTag(itemstack);
		if (nbt != null) {
			nbt.setInteger("material", Block.getIdFromBlock(e));
		}
		return itemstack;
	}

	default int getColor(ItemStack itemstack) {
		return itemstack.getItemDamage() & (16 - 1);
	}

	default ItemStack setColor(ItemStack itemstack, int i) {
		itemstack.setItemDamage(i);
		return itemstack;
	}
}
