package com.mito.exobj.module.main;

import com.mito.exobj.module.item.ItemBrace;

import com.mito.exobj.utilities.MyLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class MitoShapelessRecipe implements IRecipe {

	//private ItemStack recipeItem = new ItemStack(mitomain.ArrayItemBrace);
	private ItemStack outItem = new ItemStack(ResisterItem.ItemBrace);

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean allEmpty = true;
		int dyeNum = 0;
		int braceNum = 0;
		boolean ism = false;
		Block material = null;
		for (int h = 0; h < 3; h++) {
			for (int w = 0; w < 3; w++) {
				ItemStack current = inv.getStackInRowAndColumn(h, w);

				if (current == null) {
					allEmpty &= true;
					continue;
				} else {
					allEmpty &= false;
				}

				if (current.getItem() instanceof ItemBrace) {
					ItemBrace itembrace = (ItemBrace) current.getItem();
					if (material != null && material != itembrace.getMaterial(current)) {
						return false;
					}
					material = itembrace.getMaterial(current);
					braceNum++;
				} else if (current.getItem() == Items.DYE) {
					if (dyeNum != 0) {
						return false;
					}
					dyeNum++;
				} else if (Block.getBlockFromItem(current.getItem()) != null) {
					if (dyeNum != 0) {
						return false;
					}
					ism = true;
					dyeNum++;
				} else {
					return false;
				}
			}
		}
		if (!ism && dyeNum == 1 && material != null && !(material instanceof BlockColored)) {
			return false;
		}
		boolean flag1 = (dyeNum == 1 && braceNum == 0);
		return (!allEmpty && !flag1);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int braceNum = 0;
		int bracesize = 0;
		String mode = "";
		int dye = -1;
		Block material = null;
		int materialColor = 0;
		Block braceMaterial = null;
		String braceJoint = null;
		int braceColor = -1;
		int totalSize = 0;
		int place = 1;
		ItemBrace brace = (ItemBrace) ResisterItem.ItemBrace;
		ItemStack itemstack;

		for (int h = 0; h < 3; h++) {
			for (int w = 0; w < 3; w++) {
				if (inv.getStackInRowAndColumn(h, w) != null) {
					itemstack = inv.getStackInRowAndColumn(h, w);
					if (itemstack.getItem() instanceof ItemBrace) {
						bracesize = brace.getSize(itemstack);
						braceJoint = brace.getJoint(itemstack);
						mode = brace.getType(itemstack);
						braceColor = brace.getColor(itemstack);
						braceMaterial = brace.getMaterial(itemstack);
						braceNum++;
						totalSize += bracesize;
						place = h * 3 + w;
					} else if (inv.getStackInRowAndColumn(h, w).getItem() == Items.DYE) {
						dye = brace.getColor(itemstack);
					} else if (Block.getBlockFromItem(itemstack.getItem()) != null) {
						MyLogger.info(Block.getBlockFromItem(itemstack.getItem()).getLocalizedName());
						material = Block.getBlockFromItem(itemstack.getItem());
						materialColor = itemstack.getItemDamage();
					}
				}
			}
		}

		if (dye != -1) {
			MyLogger.info("dye");
			ItemStack itemstack1 = new ItemStack(brace, braceNum, dye);
			brace.setMaterial(itemstack1, braceMaterial);
			brace.setSize(itemstack1, bracesize);
			brace.setType(itemstack1, mode);
			brace.setJoint(itemstack1, braceJoint);
			return itemstack1;
		} else if (material != null) {
			MyLogger.info("block");
			ItemStack itemstack1 = new ItemStack(brace, braceNum, materialColor);
			brace.setMaterial(itemstack1, material);
			brace.setSize(itemstack1, bracesize);
			brace.setType(itemstack1, mode);
			brace.setJoint(itemstack1, braceJoint);
			return itemstack1;
		} /*else if (braceNum == 1) {
			String mode1 = BraceTypeRegistry.shapeList.get(place%BraceTypeRegistry.shapeList.size());//(mode + place >= brace.typeMax) ? mode + place - brace.typeMax : mode + place;
			ItemStack itemstack1 = new ItemStack(brace, braceNum, braceColor);
			brace.setMaterial(itemstack1, braceMaterial);
			brace.setSize(itemstack1, bracesize);
			brace.setType(itemstack1, mode1);
			brace.setJoint(itemstack1, braceJoint);
			return itemstack1;
		} */ else {
			ItemStack itemstack1 = new ItemStack(brace, braceNum, braceColor);

			MyLogger.info("size");
			if (totalSize > ItemBrace.sizeMax) {
				totalSize = ItemBrace.sizeMax;
			} else if (totalSize < -1) {
				totalSize = 0;
			}
			brace.setMaterial(itemstack1, braceMaterial);
			brace.setSize(itemstack1, totalSize);
			brace.setType(itemstack1, mode);
			brace.setJoint(itemstack1, braceJoint);
			return itemstack1;
		}
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
