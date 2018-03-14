package com.mito.exobj.lib.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.render.VBOList;
import com.mito.exobj.utilities.MyLogger;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BB_DataChunk {

	public List<ExtraObject> braceList = new ArrayList<ExtraObject>();
	public World world;
	public boolean isDead = false;
	public final int xPosition;
	public final int zPosition;

	private boolean shouldUpdateRender = false;

	public VBOList buffer = new VBOList();

	public BB_DataChunk(World w, Chunk c) {
		this.world = w;
		this.xPosition = c.xPosition;
		this.zPosition = c.zPosition;
	}

	public BB_DataChunk(World w, int i, int j) {
		this.world = w;
		this.xPosition = i;
		this.zPosition = j;
	}

	public void modified() {
		if (world.isRemote) {
			this.updateRenderer();
		}
		Chunk chunk = this.world.getChunkProvider().getLoadedChunk(this.xPosition, this.zPosition);
		if (chunk != null) {
			chunk.setChunkModified();
		}
	}

	public int getLength() {
		return braceList.size();
	}

	public void removeBrace(ExtraObject base) {
		this.braceList.remove(base);
		this.modified();
	}

	public boolean addBraceBase(ExtraObject base) {
		int i = MathHelper.floor_double(base.getPos().xCoord / 16.0D);
		int j = MathHelper.floor_double(base.getPos().zCoord / 16.0D);

		if (i != this.xPosition || j != this.zPosition) {
			MyLogger.warn("Wrong location! " + base + " (at " + i + ", " + j + " instead of " + this.xPosition + ", " + this.zPosition + ")");
			return false;
		}
		base.datachunk = this;
		this.modified();
		return this.braceList.add(base);
	}

	public void setDead() {
		this.isDead = true;
	}

	public void getEntitiesWithinAABBForEntity(AxisAlignedBB boundingBox, ArrayList arraylist) {

		for (ExtraObject base : braceList) {
			if (base.interactWithAABB(boundingBox)) {
				arraylist.add(base);
			}
		}

	}

	public boolean isShouldUpdateRender() {
		return shouldUpdateRender;
	}

	public void updateRenderer() {
		setShouldUpdateRender(true);
	}

	public void setShouldUpdateRender(boolean shouldUpdateRender) {
		this.shouldUpdateRender = shouldUpdateRender;
	}

	public void remove() {
		Iterator iterator = braceList.iterator();
		while (iterator.hasNext()) {
			ExtraObject base = (ExtraObject) iterator.next();
			base.datachunk = null;
			base.removeFromWorld();
		}
		BB_DataWorld worldData = ChunkWorldManager.getWorldData(world);
		worldData.removeDataChunk(this);
	}


}
