package com.mito.exobj.lib.network;

import com.mito.exobj.lib.data.BB_DataWorld;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.data.LoadClientWorldHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeletePacketProcessor implements IMessage, IMessageHandler<DeletePacketProcessor, IMessage> {

	public int id;

	public DeletePacketProcessor() {
	}

	public DeletePacketProcessor(int id) {
		this.id = id;
	}

	@Override
	public IMessage onMessage(DeletePacketProcessor message, MessageContext ctx) {
		BB_DataWorld dataworld = LoadClientWorldHandler.INSTANCE.data;
		ExtraObject base = dataworld.getBraceBaseByID(message.id);
		if (base != null) {
			base.removeFromWorld();
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
	}

}
