package com.mito.exobj.lib.editor.gui;

import com.mito.exobj.module.main.ResisterItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotInventoryItem extends Slot {

	public SlotInventoryItem(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
	}

	@Override
	public boolean canTakeStack(EntityPlayer p_82869_1_) {
		return !(getHasStack() && (getStack().getItem() == ResisterItem.ItemBlockSetter || getStack().getItem() == ResisterItem.ItemFakeBlock));
	}
}
