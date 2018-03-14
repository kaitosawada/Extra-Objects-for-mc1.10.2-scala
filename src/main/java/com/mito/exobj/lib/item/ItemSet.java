package com.mito.exobj.lib.item;

import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemSet extends ItemBraceBase {

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.BLOCK;
	}

	@Override
	public void nbtInit(NBTTagCompound nbt, ItemStack itemstack) {
		nbt.setBoolean("activated", false);
		nbt.setDouble("setX", 0.0D);
		nbt.setDouble("setY", 0.0D);
		nbt.setDouble("setZ", 0.0D);
		nbt.setInteger("ExtraObject", -1);
	}

	@Override
	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, RayTraceResult mop, BB_Key key) {
		RayTraceResult mop1 = this.getMovingOPWithKey(itemstack, world, player, key, mop, 1.0);
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		if (!world.isRemote) {
			if (mop != null && (MyUtil.canClick(world, key, mop))) {
				if (nbt.getBoolean("activated")) {
					Vec3d end = MitoMath.copyVec3(mop.hitVec);
					Vec3d set = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
					this.onActiveClick(world, player, itemstack, mop1, set, end, nbt);
					this.nbtInit(nbt, itemstack);
				} else {
					if (this.activate(world, player, itemstack, mop1, nbt, key)) {
						nbt.setDouble("setX", mop.hitVec.xCoord);
						nbt.setDouble("setY", mop.hitVec.yCoord);
						nbt.setDouble("setZ", mop.hitVec.zCoord);
						if(MyUtil.isBrace(mop) && (key.isShiftPressed() || key.isAltPressed()))
							nbt.setInteger("ExtraObject", MyUtil.getBrace(mop).BBID);
						nbt.setBoolean("activated", true);
					}
				}
			}
		} else {
			if (mop != null && (MyUtil.canClick(world, key, mop))) {
				clientProcess(mop, itemstack);
			}
		}
	}

	public void clientProcess(RayTraceResult mop, ItemStack itemstack) {
	}

	public boolean activate(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP, NBTTagCompound nbt, BB_Key key) {
		return true;
	}

	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP, Vec3d set, Vec3d end, NBTTagCompound nbt) {
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int meta, boolean p_77663_5_) {

		NBTTagCompound nbt = itemstack.getTagCompound();

		if (nbt != null) {
			if (nbt.getBoolean("activated")) {
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					if (player.getHeldItemMainhand() != itemstack) {
						this.nbtInit(nbt, itemstack);
					}
				} else {
					this.nbtInit(nbt, itemstack);
				}
			}
		} else {
			nbt = new NBTTagCompound();
			itemstack.setTagCompound(nbt);
			this.nbtInit(nbt, itemstack);
		}
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null && nbt.getBoolean("activated")) {
			this.nbtInit(nbt, itemstack);
		}
		return true;
	}

	@Override
	public void snapDegree(RayTraceResult mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		if (nbt.getBoolean("activated")) {
			Vec3d set = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			MyUtil.snapByShiftKey(mop, set);
		}
	}
}
