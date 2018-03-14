package com.mito.exobj.lib.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.utilities.MyLogger;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LoadWorldHandler {

	public static LoadWorldHandler INSTANCE = new LoadWorldHandler();

	public LoadWorldHandler() {
	}

	public Map getMap() {
		return ChunkWorldManager.worldDataMap;
	}

	public void onLoadWorld(WorldEvent.Load e) {
		this.getMap().put(e.getWorld(), new BB_DataWorld(e.getWorld()));
	}

	public void onUnloadWorld(WorldEvent.Unload e) {
		this.getMap().remove(e.getWorld());
	}

	public void onUpdate(TickEvent.ServerTickEvent e) {
	}

	public void onChunkDataSave(ChunkDataEvent.Save e) {
		if (!ChunkWorldManager.existChunkData(e.getChunk())) {
			return;
		}
		onChunkDataSave(e.getWorld(), ChunkWorldManager.getChunkDataNew(e.getChunk()));
	}

	public void onChunkDataSave(World world, BB_DataChunk chunkData) {
		NBTTagCompound nbt = new NBTTagCompound();
		try {
			NBTTagList taglistGroup = new NBTTagList();

			for (ExtraObject exObj : chunkData.braceList) {
				NBTTagCompound nbt2 = new NBTTagCompound();
				if (exObj.writeToNBTOptional(nbt2)) {
					taglistGroup.appendTag(nbt2);
				}

				nbt.setTag("BB_Group", taglistGroup);
			}
			nbtSave(world, chunkData, nbt);
		} catch (Exception ex) {
			MyLogger.warn("chunk save error on Braces&Oscillators\n");
			ex.printStackTrace();
		}
	}

	private File getSaveDir(World world) {
		File ret = new File(world.getSaveHandler().getWorldDirectory(), "ExtraObjects");
		if (!ret.exists()) {
			ret.mkdirs();
		}
		return ret;
	}

	private NBTTagCompound nbtLoad(World world, Chunk chunk) {
		NBTTagCompound nbttagcompound = null;

		DataInputStream datainputstream = RegionFileCache.getChunkInputStream(getSaveDir(world), chunk.xPosition, chunk.zPosition);
		if (datainputstream == null) {
			return null;
		}
		try {
			nbttagcompound = CompressedStreamTools.read(datainputstream);
			datainputstream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return nbttagcompound;
	}

	private void nbtSave(World world, BB_DataChunk chunk, NBTTagCompound nbt) throws IOException {

		DataOutputStream s = RegionFileCache.getChunkOutputStream(getSaveDir(world), chunk.xPosition, chunk.zPosition);
		CompressedStreamTools.write(nbt, s);
		s.close();
	}

	public void onChunkDataLoad(ChunkDataEvent.Load e) {
		onChunkDataLoad(e.getWorld(), e.getChunk(), this.nbtLoad(e.getWorld(), e.getChunk()));
	}

	public void onChunkDataLoad(World world, Chunk chunk, NBTTagCompound nbt) {
		NBTTagList taglistGroups = nbt == null ? null : nbt.getTagList("BB_Groups", 10);
		if (taglistGroups != null) {
			for (int l = 0; l < taglistGroups.tagCount(); ++l) {
				onChunkDataLoad(world, chunk, taglistGroups.getCompoundTagAt(l));
			}
		}
		NBTTagList taglistGroup = nbt == null ? null : nbt.getTagList("BB_Group", 10);
		if (taglistGroup != null) {
			for (int n = 0; n < taglistGroup.tagCount(); ++n) {
				NBTTagCompound nbttagcompound4 = taglistGroup.getCompoundTagAt(n);
				ExtraObject base = ExtraObjectRegistry.createExObjFromNBT(nbttagcompound4, world);
				if (base != null) {
					base.addToWorld();
					//MyLogger.info("nbt associate(load) " + n1 + " : " + ExtraObjectRegistry.classToStringMapping.get(base.getClass()));
				}
			}
		}
	}

	public void onWorldTickEvent(TickEvent.WorldTickEvent e) {
		BB_DataWorld data = ChunkWorldManager.getWorldData(e.world);
		data.onUpDate();
		/*for(BB_DataChunk chunk : data.coordToDataMapping.values()){
			if(!e.world.getChunkProvider().chunkExists(chunk.xPosition, chunk.zPosition)){
				chunk.setDead();
			}
		}*/
	}

	// 重複については未処理  unload -> save

	public void onChunkLoad(ChunkEvent.Load e) {
		//mylogger.info("on load");
	}

	public void onChunkUnload(ChunkEvent.Unload e) {
		if (!ChunkWorldManager.existChunkData(e.getChunk())) {
			return;
		}
		BB_DataChunk datachunk = ChunkWorldManager.getChunkDataNew(e.getChunk());
		onChunkDataSave(e.getWorld(), datachunk);
		datachunk.remove();
	}

}
