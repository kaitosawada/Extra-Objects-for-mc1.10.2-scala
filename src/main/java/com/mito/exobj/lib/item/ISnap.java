package com.mito.exobj.lib.item;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface ISnap {

	default RayTraceResult getMovingOPWithKey(ItemStack itemstack, World world, EntityPlayer player, BB_Key key, RayTraceResult mop, double partialticks) {
		if (mop != null && MyUtil.canClick(world, key, mop)) {
			if (!key.isControlPressed()) {
				mop = this.snap(mop, itemstack, world, player, key);
			}
			if (key.isShiftPressed()) {
				this.snapDegree(mop, itemstack, world, player, key);
			}
		}
		return mop;
	}

	default RayTraceResult snap(RayTraceResult mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			MyUtil.snapBlock(mop);
		} else if (mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapper) {
				ExtraObject brace = ((EntityWrapper) mop.entityHit).base;
				brace.snap(mop);
		}
		return mop;
	}

	default void snapDegree(RayTraceResult mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {}
}
