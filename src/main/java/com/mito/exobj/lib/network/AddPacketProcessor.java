package com.mito.exobj.lib.network;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class AddPacketProcessor implements IMessage, IMessageHandler<AddPacketProcessor, IMessage> {

	public ExtraObject base;
	public int id;
	public NBTTagCompound nbt;

	public AddPacketProcessor() {
	}

	public AddPacketProcessor(ExtraObject base) {
		this.base = base;
	}

	@Override
	public IMessage onMessage(AddPacketProcessor message, MessageContext ctx) {
		World world = Main.proxy.getClientWorld();
		ExtraObject base1 = message.base;
		if (base1 != null) {
			if (!ChunkWorldManager.getWorldData(world).BBIDMap.containsItem(message.id)) {
				base1.addToWorld();
			}
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		try {
			PacketBuffer pb = new PacketBuffer(buf);
			this.nbt = pb.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			MyLogger.info("brace sync error");
		}
		if (this.nbt != null) {
			this.base = ExtraObjectRegistry.createExObjFromNBT(nbt, Main.proxy.getClientWorld(), this.id);
			if (this.base == null)
				MyLogger.info("brace sync null");
		} else {
			MyLogger.info("brace sync skipped");
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.base.BBID);
		new PacketBuffer(buf).writeNBTTagCompoundToBuffer(this.base.getNBTTagCompound());
	}

}
