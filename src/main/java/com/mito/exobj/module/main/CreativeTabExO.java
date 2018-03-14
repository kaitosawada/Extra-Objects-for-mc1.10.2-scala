package com.mito.exobj.module.main;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabExO extends CreativeTabs {

	public CreativeTabExO(String label) {
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		return ResisterItem.ItemBar;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return "ExtraObjects";
	}


}
