package com.mito.exobj.lib.render;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.item.IHighLight;
import com.mito.exobj.lib.item.ISnap;
import com.mito.exobj.module.main.ClientProxy;
import com.mito.exobj.module.main.IEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BraceHighLightHandler implements IEventHandler {

	ClientProxy proxy;
	public Object key = null;
	public VBOList buffer = new VBOList();

	public BraceHighLightHandler(ClientProxy p) {
		this.proxy = p;
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
		ItemStack stack = e.getPlayer().getHeldItemMainhand();
		if (stack != null && (stack.getItem() instanceof IHighLight)) {
			RayTraceResult mop = e.getTarget();
			if(stack.getItem() instanceof ISnap && Minecraft.getMinecraft().currentScreen == null){
				mop = ((ISnap)stack.getItem()).getMovingOPWithKey(stack, e.getPlayer().worldObj, e.getPlayer(), proxy.getKey(), mop, e.getPartialTicks());
			}
			IHighLight itembrace = (IHighLight) stack.getItem();
			boolean flag = itembrace.drawHighLight(stack, e.getPlayer(), e.getPartialTicks(), mop);
			if (flag) {
				if (e.isCancelable()) {
					e.setCanceled(true);
				}
			}
		} else if (e.getPlayer().capabilities.isCreativeMode) {
			if (MyUtil.isBrace(e.getTarget())) {
				GL11.glPushMatrix();
				drawHighLightBrace(e.getPlayer(), MyUtil.getBrace(e.getTarget()), e.getPartialTicks());
				GL11.glPopMatrix();
				if (e.isCancelable()) {
					e.setCanceled(true);
				}
			}
		}
	}

	public void drawHighLightBrace(EntityPlayer player, ExtraObject base, float partialticks) {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
		BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
		render.drawHighLight(base, partialticks);
		GL11.glPopMatrix();
	}

}
