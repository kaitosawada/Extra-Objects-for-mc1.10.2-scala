package com.mito.exobj.lib.network;

import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.item.IMouseWheel;
import com.mito.exobj.lib.item.ISnap;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MouseWheelPacketProcessor implements IMessage, IMessageHandler<MouseWheelPacketProcessor, IMessage> {

	private int slot;
	private int dwheel;
	private BB_Key key;

	public MouseWheelPacketProcessor() {
	}

	public MouseWheelPacketProcessor(int slot, BB_Key key, int dwheel) {
		this.slot = slot;
		this.dwheel = dwheel;
		this.key = key;
	}

	@Override
	public IMessage onMessage(MouseWheelPacketProcessor message, MessageContext ctx) {
		try {
			ItemStack stack = null;
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			World world = DimensionManager.getWorld(player.dimension);
			if (message.slot > -1 && message.slot < 9) {
				stack = ctx.getServerHandler().playerEntity.inventory.getStackInSlot(message.slot);
			}
			if (stack != null && stack.getItem() != null && stack.getItem() instanceof IMouseWheel) {
				RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
				if (stack.getItem() instanceof ISnap) {
					mop = ((ISnap)stack.getItem()).getMovingOPWithKey(stack, world, player, Main.proxy.getKey(), mop, 1.0);
				}
				((IMouseWheel) stack.getItem()).wheelEvent(ctx.getServerHandler().playerEntity, stack, mop, message.key, message.dwheel);
			}
		} finally {}

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		dwheel = buf.readInt();
		key = new BB_Key(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeInt(dwheel);
		buf.writeInt(key.ikey);
	}

}
