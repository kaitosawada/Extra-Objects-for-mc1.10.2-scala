package com.mito.exobj.lib.network;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.data.BB_DataWorld;
import com.mito.exobj.lib.data.LoadClientWorldHandler;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class SyncPacketProcessor implements IMessage, IMessageHandler<SyncPacketProcessor, IMessage> {

	public ExtraObject base;
	public int id;
	public NBTTagCompound nbt;

	public SyncPacketProcessor() {
	}

	public SyncPacketProcessor(ExtraObject eo) {
		base = eo;
	}

	@Override
	public IMessage onMessage(SyncPacketProcessor message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			World world = Main.proxy.getClientWorld();
			BB_DataWorld dataworld = LoadClientWorldHandler.INSTANCE.data;

			if (message.nbt != null) {
				message.base = ExtraObjectRegistry.syncBraceBaseFromNBT(message.nbt, world, message.id);
				if (message.base == null) {
					MyLogger.info("brace sync null");
				} else {
					message.base.updateRenderer();
				}
			} else {
				MyLogger.info("brace sync skipped");
			}

		} else {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			World world = DimensionManager.getWorld(player.dimension);

			if (message.nbt != null) {
				message.base = ExtraObjectRegistry.syncBraceBaseFromNBT(message.nbt, world, message.id);

				if (message.base == null)
					MyLogger.info("brace sync null");
			} else {
				MyLogger.info("brace sync skipped");
			}
			PacketHandler.INSTANCE.sendToAll(new SyncPacketProcessor(message.base));
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
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.base.BBID);
		PacketBuffer pb1 = new PacketBuffer(buf);
		NBTTagCompound nbt1 = new NBTTagCompound();
		this.base.writeToNBTOptional(nbt1);
		pb1.writeNBTTagCompoundToBuffer(nbt1);
	}

}
