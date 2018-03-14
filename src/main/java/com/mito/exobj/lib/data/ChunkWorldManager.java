package com.mito.exobj.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mito.exobj.lib.ExtraObject;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

public class ChunkWorldManager {

	public static List<Integer> Incomplist = new ArrayList<Integer>();
	public static Map<WorldServer, BB_DataWorld> worldDataMap = new HashMap<WorldServer, BB_DataWorld>();

	@Nullable
	public static BB_DataWorld getWorldData(World world) {
		if (world == null) {
			return null;
		}
		if (world.isRemote) {
			return LoadClientWorldHandler.INSTANCE.data;
		} else {
			return worldDataMap.get(world);
		}
	}

	public static boolean existChunkData(Chunk chunk) {
		if (chunk == null) {
			return false;
		}
		return getWorldData(chunk.getWorld()).coordToDataMapping.containsKey(ChunkPos.asLong(chunk.xPosition, chunk.zPosition));
	}

	public static boolean existChunkData(World world, int i, int j) {
		if (world == null) {
			return false;
		}
		return getWorldData(world).coordToDataMapping.containsKey(ChunkPos.asLong(i, j));
	}

	public static BB_DataChunk getChunkDataNew(Chunk chunk) {
		if (chunk == null) {
			return null;
		}
		return ChunkWorldManager.getChunkDataNew(chunk.getWorld(), chunk.xPosition, chunk.zPosition);
	}

	public static BB_DataChunk getChunkDataNew(World world, int i, int j) {
		BB_DataChunk ret;
		ret = getWorldData(world).coordToDataMapping.get(ChunkPos.asLong(i, j));
		if (ret == null) {
			//world.getChunkProvider().chunkExists(i, j);
			ret = ChunkWorldManager.newDataChunk(world, i, j);
		}
		return ret;
	}

	public static ExtraObject getFixedObj(World world, int id) {
		if (world == null) {
			return null;
		}
		return getWorldData(world).getBraceBaseByID(id);
	}

	public static BB_DataChunk newDataChunk(Chunk chunk) {
		if (chunk == null) {
			return null;
		}
		return newDataChunk(chunk.getWorld(), chunk.xPosition, chunk.zPosition);
	}

	public static BB_DataChunk newDataChunk(World world, int i, int j) {
		BB_DataChunk ret = new BB_DataChunk(world, i, j);
		getWorldData(world).coordToDataMapping.put(ChunkPos.asLong(i, j), ret);
		return ret;
	}

	public static boolean isChunkExist(World world, int i, int j) {
		return ChunkWorldManager.getWorldData(world).coordToDataMapping.containsKey(ChunkPos.asLong(i, j));
	}
}
