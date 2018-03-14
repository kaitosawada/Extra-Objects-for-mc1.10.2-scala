package com.mito.exobj.module.item;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.item.IHighLight;
import com.mito.exobj.lib.item.IMaterial;
import com.mito.exobj.lib.item.ItemSet;
import com.mito.exobj.module.exobject.Tofu;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.render.RenderHighLight;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemTofu extends ItemSet implements IHighLight, IMaterial {

	public ItemTofu() {
		super();
		this.setMaxDamage(0);
	}

	@Override
	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP,
							  Vec3d set, Vec3d end, NBTTagCompound nbt) {
		Tofu tofu = new Tofu(world, set, end, getMaterial(itemstack), getColor(itemstack));
		tofu.addToWorld();

		if (!player.capabilities.isCreativeMode) {
			itemstack.stackSize--;
			if (itemstack.stackSize == 0) {
				//player.destroyCurrentEquippedItem();
			}
		}
	}

	@Override
	public boolean drawHighLight(ItemStack itemStack, EntityPlayer player, float partialticks,
									RayTraceResult mop) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			if (mop == null)
				return false;
			Vec3d set = mop.hitVec;
			RenderHighLight rh = RenderHighLight.INSTANCE;
			NBTTagCompound nbt = getNBTShareTag(itemStack);
			if (nbt.getBoolean("activated") && MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop)) {
				Vec3d end = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
				rh.drawBox(player, set, end, partialticks);
				return true;
			} else {
				return this.drawHighLightBrace(player, partialticks, mop);
			}
		}
		return false;
	}

}
