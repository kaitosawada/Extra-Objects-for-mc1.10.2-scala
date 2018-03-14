package com.mito.exobj.lib.network;

import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.ExtraObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestPacketProcessor implements IMessage, IMessageHandler<RequestPacketProcessor, IMessage> {

	public int id;

	public RequestPacketProcessor() {
	}

	public RequestPacketProcessor(int id) {
		this.id = id;
	}

	@Override
	public IMessage onMessage(RequestPacketProcessor message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = DimensionManager.getWorld(player.dimension);
		if (ChunkWorldManager.getWorldData(world).BBIDMap.containsItem(message.id)) {
			PacketHandler.INSTANCE.sendTo(new AddPacketProcessor((ExtraObject) ChunkWorldManager.getWorldData(world).BBIDMap.lookup(message.id)), player);
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
