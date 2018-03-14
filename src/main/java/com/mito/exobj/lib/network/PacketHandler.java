package com.mito.exobj.lib.network;

import com.mito.exobj.lib.network.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static int nex = 0;
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("mito_exobj");


	public static void init() {
		INSTANCE.registerMessage(AddPacketProcessor.class, AddPacketProcessor.class, nex++, Side.CLIENT);
		INSTANCE.registerMessage(DeletePacketProcessor.class, DeletePacketProcessor.class, nex++, Side.CLIENT);
		INSTANCE.registerMessage(SuggestPacketProcessor.class, SuggestPacketProcessor.class, nex++, Side.CLIENT);
		INSTANCE.registerMessage(SyncPacketProcessor.class, SyncPacketProcessor.class, nex++, Side.CLIENT);
		INSTANCE.registerMessage(SyncPacketProcessor.class, SyncPacketProcessor.class, nex++, Side.SERVER);
		INSTANCE.registerMessage(RequestChunkPacketProcessor.class, RequestChunkPacketProcessor.class, nex++, Side.SERVER);
		INSTANCE.registerMessage(RequestPacketProcessor.class, RequestPacketProcessor.class, nex++, Side.SERVER);

		INSTANCE.registerMessage(MouseWheelPacketProcessor.class, MouseWheelPacketProcessor.class, nex++, Side.SERVER);
		INSTANCE.registerMessage(ClickPacketProcessor.class, ClickPacketProcessor.class, nex++, Side.SERVER);
		INSTANCE.registerMessage(EditerPacketProcessor.class, EditerPacketProcessor.class, nex++, Side.SERVER);
	}

}
