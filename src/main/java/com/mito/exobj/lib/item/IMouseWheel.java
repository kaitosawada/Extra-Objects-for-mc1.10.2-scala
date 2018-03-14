package com.mito.exobj.lib.item;

import com.mito.exobj.lib.input.BB_Key;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

public interface IMouseWheel {
	boolean wheelEvent(EntityPlayer player, ItemStack stack, RayTraceResult mop, BB_Key key, int dwheel);
}
