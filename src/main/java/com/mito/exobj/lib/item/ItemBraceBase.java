package com.mito.exobj.lib.item;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.module.main.Main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/*
①onItemUseとonItemRightClickをまとめる
②標準パラメタ設定
*/

public abstract class ItemBraceBase extends Item implements ISnap {

	public ItemBraceBase() {
		super();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack itemStackIn, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		RayTraceResult mop = this.getMovingOPWithKey(itemStackIn, worldIn, playerIn, Main.proxy.getKey(), Minecraft.getMinecraft().objectMouseOver, 1.0);
		this.RightClick(itemStackIn, worldIn, playerIn, mop, Main.proxy.getKey());
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		RayTraceResult mop = this.getMovingOPWithKey(itemStackIn, worldIn, playerIn, Main.proxy.getKey(), Minecraft.getMinecraft().objectMouseOver, 1.0);
		this.RightClick(itemStackIn, worldIn, playerIn, mop, Main.proxy.getKey());
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, RayTraceResult mop, BB_Key key) {}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			itemstack.setTagCompound(nbt);
			this.nbtInit(nbt, itemstack);
		}

		return nbt;
	}

	public void nbtInit(NBTTagCompound nbt, ItemStack itemstack) {
	}
}
