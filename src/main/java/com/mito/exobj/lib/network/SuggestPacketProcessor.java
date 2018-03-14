package com.mito.exobj.lib.network;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.main.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SuggestPacketProcessor implements IMessage, IMessageHandler<SuggestPacketProcessor, IMessage> {

	public int id;
	public int xChunkCoord;
	public int zChunkCoord;
	public int DimensionID;

	public SuggestPacketProcessor() {
	}

	public SuggestPacketProcessor(ExtraObject eo) {
		this.id = eo.BBID;
		this.xChunkCoord = MathHelper.floor_double(eo.getPos().xCoord / 16.0D);
		this.zChunkCoord = MathHelper.floor_double(eo.getPos().zCoord / 16.0D);
		this.DimensionID = eo.worldObj.provider.getDimension();
	}

	@Override
	public IMessage onMessage(SuggestPacketProcessor message, MessageContext ctx) {
		World world = Main.proxy.getClientWorld();
		if (world != null && world.provider.getDimension() == message.DimensionID) {
			return new RequestPacketProcessor(message.id);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.DimensionID = buf.readInt();
		this.xChunkCoord = buf.readInt();
		this.zChunkCoord = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeInt(this.DimensionID);
		buf.writeInt(this.xChunkCoord);
		buf.writeInt(this.zChunkCoord);
	}

}
