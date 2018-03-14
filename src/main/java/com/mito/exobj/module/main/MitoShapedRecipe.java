package com.mito.exobj.module.main;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class MitoShapedRecipe implements IRecipe {

	private ItemStack recipeItem = new ItemStack(Blocks.DIRT);
	private ItemStack outItem = new ItemStack(Items.DYE);

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean allEmpty = true;
		for (int h = 0; h < 3; h++) {
			for (int w = 0; w < 3; w++) {
				ItemStack current = inv.getStackInRowAndColumn(h, w);
				if (current == null) {
					allEmpty &= true;
					continue;
				} else
					allEmpty &= false;
				if (!(current.getItem() == recipeItem.getItem())) {
					return false;
				}
			}
		}
		return !allEmpty;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int outNum = 0;
		for (int h = 0; h < 3; h++) {
			for (int w = 0; w < 3; w++) {
				if (inv.getStackInRowAndColumn(h, w) != null)
					outNum++;
			}
		}
		if (outNum >= 5) {
			outNum = 4;
		}
		return new ItemStack(ResisterItem.ItemBrace);
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return outItem;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		return null;
	}

}
