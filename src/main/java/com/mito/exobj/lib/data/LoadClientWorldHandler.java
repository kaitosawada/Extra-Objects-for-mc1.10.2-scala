package com.mito.exobj.lib.data;

import java.util.Iterator;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.main.ConfigManager;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.network.PacketHandler;

import com.mito.exobj.lib.network.RequestChunkPacketProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LoadClientWorldHandler {

	public BB_DataWorld data;
	public static LoadClientWorldHandler INSTANCE = new LoadClientWorldHandler();

	public LoadClientWorldHandler() {
	}

	public static void onChunkUpdate(World world, BlockPos pos) {
		//MyLogger.info("m");
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (!ChunkWorldManager.existChunkData(Main.proxy.getClientWorld(), i / 16, k / 16)) {
			return;
		}
		BB_DataChunk ret = ChunkWorldManager.getChunkDataNew(world, i / 16, k / 16);
		if (ret != null) {
			ret.updateRenderer();
		}
	}

	public void onUnloadWorld(WorldEvent.Unload e) {
		this.data = null;
		Main.proxy.sg.init();
	}

	public void onLoadWorld(WorldEvent.Load e) {
		this.data = new BB_DataWorld(e.getWorld());
		Main.quality = ConfigManager.quality();
	}

	public void onUpdate(TickEvent.PlayerTickEvent e) {
		if (this.data != null)
			this.data.onUpDate();
	}

	public void onChunkLoad(ChunkEvent.Load e) {
		PacketHandler.INSTANCE.sendToServer(new RequestChunkPacketProcessor(e.getChunk().xPosition, e.getChunk().zPosition));
	}

	public void onChunkUnload(ChunkEvent.Unload e) {
		BB_DataChunk chunkData = ChunkWorldManager.getChunkDataNew(e.getChunk());
		Iterator iterator = chunkData.braceList.iterator();
		while (iterator.hasNext()) {
			ExtraObject fobj = (ExtraObject) iterator.next();
			fobj.datachunk = null;
			fobj.removeFromWorld();
		}
		data.removeDataChunk(chunkData);
	}

}
