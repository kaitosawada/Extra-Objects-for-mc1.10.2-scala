package com.mito.exobj.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.render.VBOList;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.lib.network.DeletePacketProcessor;
import com.mito.exobj.lib.network.PacketHandler;

import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BB_DataWorld {

	public EntityWrapper wrapper;
	public IntHashMap BBIDMap = new IntHashMap();
	public List<ExtraObject> braceBaseList = new ArrayList<ExtraObject>();
	public Map<Long, BB_DataChunk> coordToDataMapping = new HashMap<Long, BB_DataChunk>();
	public World world;
	private double MAX_ENTITY_RADIUS = 100;
	public boolean shouldUpdateRender = false;
	//@SideOnly(Side.CLIENT)
	public VBOList buffer = new VBOList();

	/*public BB_DataWorld() {
	}*/

	public BB_DataWorld(World world) {
		this.world = world;
		this.wrapper = new EntityWrapper(world, null);
	}

	//boolean b はチャンクに追加するかどうか

	public boolean addBraceBase(ExtraObject base, boolean b) {
		this.BBIDMap.addKey(base.BBID, base);
		if (b) {
			int i = MathHelper.floor_double(base.getPos().xCoord / 16.0D);
			int j = MathHelper.floor_double(base.getPos().zCoord / 16.0D);

			BB_DataChunk datachunk = ChunkWorldManager.getChunkDataNew(world, i, j);

			if (!this.braceBaseList.add(base)) {
				MyLogger.info("can not add worldlist");
				return false;
			}
			if (!datachunk.addBraceBase(base)) {
				this.braceBaseList.remove(base);
				return false;
			}
			if (world.isRemote)
				this.shouldUpdateRender = true;
			return true;
		} else {
			return this.braceBaseList.add(base);
		}
	}

	public boolean removeBrace(ExtraObject base) {
		this.BBIDMap.removeObject(base.BBID);
		if (base.datachunk != null) {
			base.datachunk.removeBrace(base);
		}
		boolean ret = braceBaseList.remove(base);
		if (!this.world.isRemote) {
			PacketHandler.INSTANCE.sendToAll(new DeletePacketProcessor(base.BBID));
		}
		if (world.isRemote)
			this.shouldUpdateRender = true;

		return ret;
	}

	public ExtraObject getBraceBaseByID(int id) {
		return (ExtraObject) this.BBIDMap.lookup(id);
	}

	final public List<ExtraObject> deleteList = new ArrayList<ExtraObject>();

	public void onUpDate() {
		for(ExtraObject e : deleteList){
			e.removeFromWorld();
		}
		deleteList.clear();
		for (int n = 0; n < this.braceBaseList.size(); n++) {
			ExtraObject base = this.braceBaseList.get(n);
			if (base.isDead) {
				//base.removeFromWorld();
				deleteList.add(base);
				continue;
			}
			base.onUpdate();
		}
	}

	public void removeDataChunk(BB_DataChunk d) {
		this.coordToDataMapping.remove(ChunkPos.asLong(d.xPosition, d.zPosition));
	}

	public List<ExtraObject> getExtraObjectWithAABB(AxisAlignedBB boundingBox) {
		ArrayList<ExtraObject> arraylist = new ArrayList<ExtraObject>();
		int i = MathHelper.floor_double((boundingBox.minX - MAX_ENTITY_RADIUS) / 16.0D);
		int j = MathHelper.floor_double((boundingBox.maxX + MAX_ENTITY_RADIUS) / 16.0D);
		int k = MathHelper.floor_double((boundingBox.minZ - MAX_ENTITY_RADIUS) / 16.0D);
		int l = MathHelper.floor_double((boundingBox.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

		for (int i1 = i; i1 <= j; ++i1) {
			for (int j1 = k; j1 <= l; ++j1) {
				if (ChunkWorldManager.isChunkExist(world, i1, j1)) {
					ChunkWorldManager.getChunkDataNew(world, i1, j1).getEntitiesWithinAABBForEntity(boundingBox, arraylist);
				}
			}
		}

		return arraylist;
	}
}
