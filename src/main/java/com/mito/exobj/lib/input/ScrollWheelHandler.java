package com.mito.exobj.lib.input;

import com.mito.exobj.lib.item.IMouseWheel;
import com.mito.exobj.lib.item.ISnap;
import com.mito.exobj.lib.network.MouseWheelPacketProcessor;
import com.mito.exobj.module.main.ClientProxy;
import com.mito.exobj.lib.network.PacketHandler;

import com.mito.exobj.module.main.IEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ScrollWheelHandler implements IEventHandler {

	ClientProxy proxy;
	int sizeNum = 4;

	public ScrollWheelHandler(ClientProxy p) {
		proxy = p;
	}

	//1:ctrl 2:shift 4:alt

	@SubscribeEvent
	public void mouseEvent(MouseEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (event.getDwheel() != 0 && player != null) {
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof IMouseWheel) {
					RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
					if (item instanceof ISnap) {
						mop = ((ISnap)item).getMovingOPWithKey(stack, player.worldObj, player, proxy.getKey(), Minecraft.getMinecraft().objectMouseOver, 1.0);
					}
					boolean flag = ((IMouseWheel) item).wheelEvent(player, stack, mop, proxy.getKey(), event.getDwheel());
					event.setCanceled(flag);
					if (flag)
						PacketHandler.INSTANCE.sendToServer(new MouseWheelPacketProcessor(player.inventory.currentItem, proxy.getKey(), event.getDwheel()));
				}
			}
		}

	}

	/*private void changeSizeBar(ItemStack stack, EntityPlayer player, int dWheel) {

		int w = dWheel / 120;
		int size = stack.getItemDamage() + w;
		if (size >= sizeNum) {
			size = size % sizeNum;
		} else if (size < 0) {
			size = (size + sizeNum * 500) % sizeNum;
		}

		stack.setItemDamage(size);
		//PacketHandler.INSTANCE.sendToServer(new MouseWheelPacketProcessor(player.inventory.currentItem, size, (byte) 2));
	}

	private void changeSelect(ItemStack stack, EntityPlayer player, int dWheel) {

		int w = dWheel / 120;
		int size = stack.getTagCompound().getInteger("selectNum") + w;
		if (size < 0) {
			size = 50000;
		} else if (size > 50000) {
			size = 0;
		}

		stack.getTagCompound().setInteger("selectNum", size);

		//PacketHandler.INSTANCE.sendToServer(new MouseWheelPacketProcessor(player.inventory.currentItem, size, (byte) 4));
	}*/

}
