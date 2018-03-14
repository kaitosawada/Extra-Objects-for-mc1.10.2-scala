package com.mito.exobj.lib.item;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.render.BB_Render;
import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

public interface IHighLight {
	boolean drawHighLight(ItemStack itemstack, EntityPlayer player, float partialTick, RayTraceResult mop);

	default boolean drawHighLightBrace(EntityPlayer player, float partialticks, RayTraceResult mop) {
		if (mop != null) {
			if (MyUtil.isBrace(mop)) {
				ExtraObject base = ((EntityWrapper) mop.entityHit).base;
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
						-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
						-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
				//BB_Render render = ExtraObjectRegistry.getBraceBaseRender(brace);
				BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
				render.drawHighLight(base, partialticks);
				GL11.glPopMatrix();
				return true;
			}
		}
		return false;
	}
}
