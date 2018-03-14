package com.mito.exobj.lib.item;

import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

public class ItemBar extends ItemBraceBase implements IMouseWheel, IHighLight {

	/*int size = 2;
	public double[] sizeArray = {0.2, 1.0, 3.0, 0.005};*/

	public ItemBar() {
		super();
		this.maxStackSize = 1;
		this.setMaxDamage(3);

	}

	@Override
	public boolean drawHighLight(ItemStack itemstack, EntityPlayer player, float partialticks, RayTraceResult mop) {
		return this.drawHighLightBrace(player, partialticks, mop);
	}

	@Override
	public boolean wheelEvent(EntityPlayer player, ItemStack stack, RayTraceResult mop, BB_Key key, int dwheel) {
		if (key.isShiftPressed()) {
			if (mop != null) {
				if (MyUtil.isBrace(mop) && ((EntityWrapper) mop.entityHit).base instanceof Brace) {
					Brace brace = (Brace) ((EntityWrapper) mop.entityHit).base;
					int w = dwheel / 120;
					double div = brace.getRoll() + (double) w * 15;
					if (div < 0) {
						div = div + 360;
					} else if (div > 360) {
						div = div - 360;
					}
					brace.setRoll(div);
					return true;
				}
			}
		}
		return false;
	}

}
