package com.mito.exobj.lib.editor;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.render.VBOList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileObjects extends TileEntity {
	public List<ExtraObject> list = null;
	@SideOnly(Side.CLIENT)
	public VBOList buffer = new VBOList();
	public boolean shouldUpdateRender = true;

	public TileObjects() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		list = new ArrayList<>();
		super.readFromNBT(nbt);
		NBTTagList taglist = nbt.getTagList("BB_Groups", 10);
		for (int i1 = 0; i1 < taglist.tagCount(); ++i1) {
			NBTTagCompound nbt1 = taglist.getCompoundTagAt(i1);
			ExtraObject eo = ExtraObjectRegistry.createExObjFromNBT(nbt1, this.worldObj);
			list.add(eo);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList taglist = new NBTTagList();
		if (list != null) {
			for (ExtraObject eo : list) {
				NBTTagCompound nbt1 = new NBTTagCompound();
				eo.writeToNBTOptional(nbt1);
				taglist.appendTag(nbt1);
			}
			nbt.setTag("BB_Groups", taglist);
		}
		return nbt;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new SPacketUpdateTileEntity(this.pos, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		//MyLogger.info("onDataPacket");
		this.readFromNBT(pkt.getNbtCompound());
	}

	public void breakBrace() {
		if (list != null) {
			Vec3d v = new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ());
			for (ExtraObject base : list) {
				ExtraObject eo1 = base.copy(worldObj);
				eo1.translate(v);
				eo1.addToWorld();
			}
		}
	}

	public boolean isShouldUpdateRender() {
		return shouldUpdateRender;
	}

	public void setShouldUpdateRender(boolean shouldUpdateRender) {
		this.shouldUpdateRender = shouldUpdateRender;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		AxisAlignedBB ret = null;
		for (ExtraObject base : list) {
			AxisAlignedBB aabb = base.getBoundingBox().addCoord(getPos().getX(), getPos().getY(), getPos().getZ());
			if(ret == null){
				ret = aabb;
			} else if(aabb != null) {
				ret = ret.union(aabb);
			}
		}
		if(ret == null){
			ret = super.getRenderBoundingBox();
		}
		return ret;
	}
}
