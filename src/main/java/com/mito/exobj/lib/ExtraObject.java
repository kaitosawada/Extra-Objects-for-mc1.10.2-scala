package com.mito.exobj.lib;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.mito.exobj.lib.data.BB_DataChunk;
import com.mito.exobj.lib.data.BB_DataWorld;
import com.mito.exobj.lib.network.SyncPacketProcessor;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.render.BraceHighLightHandler;
import com.mito.exobj.lib.render.model.ITransform;
import com.mito.exobj.module.main.ClientProxy;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.lib.network.PacketHandler;
import com.mito.exobj.lib.network.SuggestPacketProcessor;
import com.mito.exobj.lib.render.model.Line;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ExtraObject implements ITransform {

	public static int nextID;
	public Vec3d rand = new Vec3d(Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001);
	public boolean isDead = false;
	protected Vec3d pos;
	//public Vec3d motion;
	public World worldObj;
	public UUID uuid;
	//public int fire;
	public int BBID;
	private NBTTagCompound customFixedObjectData;

	public BB_DataWorld dataworld;
	public BB_DataChunk datachunk;

	public Random random = new Random();

	public ExtraObject(World world, Vec3d pos) {
		this(world);
		this.pos = pos;
	}

	public ExtraObject(World world) {
		this.worldObj = world;
		this.BBID = nextID++;
		this.dataworld = ChunkWorldManager.getWorldData(worldObj);
		if (dataworld == null) {
			MyLogger.warn("bracebase data world is null");
		}
		this.uuid = UUID.randomUUID();
	}

	public boolean addToWorld() {
		if (worldObj == null) {
			return false;
		}
		if (worldObj.isRemote) {
			return dataworld.addBraceBase(this, true);
		} else {
			boolean ret;
			if (pos != null) {
				ret = dataworld.addBraceBase(this, true);
			} else {
				ret = dataworld.addBraceBase(this, false);
			}
			if (ret) {
				PacketHandler.INSTANCE.sendToAll(new SuggestPacketProcessor(this));
			}
			return ret;
		}
	}

	public boolean removeFromWorld() {
		if (this.datachunk != null) {
			if (datachunk.braceList.isEmpty())
				dataworld.removeDataChunk(datachunk);
		}
		return dataworld.removeBrace(this);
	}

	public void changeId(int id) {
		if (id != this.BBID) {
			this.dataworld.BBIDMap.removeObject(this.BBID);
			this.BBID = id;
			this.dataworld.BBIDMap.addKey(this.BBID, this);
		} else {
			return;
		}
	}

	public void setDead() {
		this.isDead = true;
	}

	public void onUpdate() {
	}

	public NBTTagCompound getNBTTagCompound() {
		if (customFixedObjectData == null) {
			customFixedObjectData = new NBTTagCompound();
			this.writeToNBTOptional(customFixedObjectData);
		}
		return customFixedObjectData;
	}

	protected NBTTagList newDoubleNBTList(double... p_70087_1_) {
		NBTTagList nbttaglist = new NBTTagList();
		double[] adouble = p_70087_1_;
		int i = p_70087_1_.length;

		for (int j = 0; j < i; ++j) {
			double d1 = adouble[j];
			nbttaglist.appendTag(new NBTTagDouble(d1));
		}

		return nbttaglist;
	}

	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		//MyLogger.info("write opt brace id " + this.BBID);
		String s = ExtraObjectRegistry.getBraceBaseString(this);

		if (!this.isDead && s != null) {
			p_70039_1_.setString("id", s);
			this.writeToNBT(p_70039_1_);
			return true;
		} else {
			return false;
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		try {
			nbt.setTag("Pos", this.newDoubleNBTList(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord));
			nbt.setBoolean("Locked", locked);
			//nbt.setShort("Fire", (short) this.fire);
			if (this.uuid != null) {
				nbt.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
				nbt.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
			}

			this.writeExtraObjectToNBT(nbt);

		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving extra object NBT");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Extra object being saved");
			//this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	public void sync() {
		if (worldObj.isRemote) {
			PacketHandler.INSTANCE.sendToServer(new SyncPacketProcessor(this));
		} else {
			PacketHandler.INSTANCE.sendToAll(new SyncPacketProcessor(this));
		}
	}

	private UUID getUniqueID() {
		return this.uuid;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = nbt.getTagList("Pos", 6);

		this.pos = new Vec3d(0, 0, 0);
		double x1 = nbttaglist.getDoubleAt(0);
		double y1 = nbttaglist.getDoubleAt(1);
		double z1 = nbttaglist.getDoubleAt(2);
		this.pos = new Vec3d(x1, y1, z1);

		this.locked = nbt.getBoolean("Locked");

		//this.fire = nbt.getShort("Fire");

		if (nbt.hasKey("UUIDMost", 4) && nbt.hasKey("UUIDLeast", 4)) {
			this.uuid = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));
		}

		this.setPosition(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord);

		if (nbt.hasKey("PersistentIDMSB") && nbt.hasKey("PersistentIDLSB")) {
			this.uuid = new UUID(nbt.getLong("PersistentIDMSB"), nbt.getLong("PersistentIDLSB"));
		}
		this.readExtraObjectFromNBT(nbt);

		/*if (this.shouldSetPosAfterLoading()) {
			this.setPosition(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord);
		}*/
	}

	private void setPosition(double xCoord, double yCoord, double zCoord) {
		this.pos = new Vec3d(xCoord, yCoord, zCoord);

	}

	/*private boolean shouldSetPosAfterLoading() {
		return true;
	}*/

	/**
	 * (abstract) Protected helper method to read subclass braceBase data from NBT.
	 */
	public abstract void readExtraObjectFromNBT(NBTTagCompound nbt);

	/**
	 * (abstract) Protected helper method to write subclass braceBase data to NBT.
	 */
	public abstract void writeExtraObjectToNBT(NBTTagCompound nbt);

	//TileEntityでのrender時に範囲内かどうか見るやつ
	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	//collision raytraceの際に大雑把にとるためのやつ
	public boolean interactWithAABB(AxisAlignedBB boundingBox) {
		boolean ret = false;
		if (boundingBox.isVecInside(pos)) {
			ret = true;
		}
		return ret;
	}

	//RayTrace
	public Line interactWithRay(Vec3d set, Vec3d end) {
		return null;
	}

	public boolean rightClick(EntityPlayer player, Vec3d pos, ItemStack itemstack) {
		if (player.capabilities.isCreativeMode) {
			if (Main.proxy.isShiftKeyDown()) {
				this.lock(player);
				return true;
			}
		}
		return false;
	}

	private boolean locked = false;

	public void lock(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			locked = !locked;
			sync();
			if (locked) player.addChatComponentMessage(new TextComponentString("This object has been locked"));
			if (!locked) player.addChatComponentMessage(new TextComponentString("This object has been unlocked"));
		}
	}

	public boolean isLocked(EntityPlayer player) {
		if (locked && worldObj.isRemote) {
			player.addChatComponentMessage(new TextComponentString("this object is locked"));
		}
		return locked;
	}

	public void updateRenderer() {
		if (worldObj.isRemote) {
			if (datachunk != null)
				this.datachunk.updateRenderer();
			//BraceHighLightHandler data = ((ClientProxy) Main.proxy).bh;
			//data.key = null;
		}
	}

	public boolean leftClick(EntityPlayer player, ItemStack itemStack) {
		if (player.capabilities.isCreativeMode) {
			//早すぎてブロック壊しちゃうのでサーバーでだけとりあえずやる
			if (!worldObj.isRemote) {
				this.breakBrace(player);
				return true;
			}
		}
		return false;
	}

	public Vec3d getPos() {
		return pos;
	}

	public void breakBrace(EntityPlayer player) {
		if (!isLocked(player)) {
			if (!player.worldObj.isRemote) {
				if (!player.capabilities.isCreativeMode) {
					this.dropItem();
				}
				this.setDead();
			} else {
			/*Main.proxy.playSound(new ResourceLocation(this.texture.getBreakSound()), this.texture.stepSound.volume, this.texture.stepSound.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
			Main.proxy.particle(this);*/
			}
		}
	}

	public void dropItem() {
	}

	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
	}

	public ExtraObject copy() {
		return copy(worldObj);
	}

	public ExtraObject copy(World world) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBTOptional(nbt);
		return ExtraObjectRegistry.createExObjFromNBT(nbt, world);
	}

	@SideOnly(Side.CLIENT)
	public void particle() {

	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float i, double x, double y, double z) {

		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(MathHelper.floor_double(x), 0, MathHelper.floor_double(z));

		if (this.worldObj.isBlockLoaded(blockPos)) {
			blockPos.setY(MathHelper.floor_double(y));
			return this.worldObj.getCombinedLight(blockPos, 0);
		} else {
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float i) {
		return getBrightnessForRender(i, pos.xCoord, pos.yCoord, pos.zCoord);
	}

	public RayTraceResult snap(RayTraceResult mop) {
		return mop;
	}
}
