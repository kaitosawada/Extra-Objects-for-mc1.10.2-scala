package com.mito.exobj.module.exobject;

import java.util.Arrays;
import java.util.List;

import com.mito.exobj.lib.render.model.BB_Model;
import com.mito.exobj.lib.render.model.IDrawable;
import com.mito.exobj.lib.render.model.Mat4;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.module.main.ResisterItem;
import com.mito.exobj.lib.render.model.Line;

import com.mito.exobj.lib.ModelObject;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Tofu extends ModelObject {

	//pos > cpoint > end
	public AxisAlignedBB aabb = null;

	public Tofu(World world) {
		super(world);
	}

	public Tofu(World world, Vec3d pos) {
		super(world, pos);
	}

	public Tofu(World world, Vec3d pos, Vec3d end, Block material, int tex) {
		this(world, pos);
		double maxX = Math.max(pos.xCoord, end.xCoord) + 0.01;
		double maxY = Math.max(pos.yCoord, end.yCoord) + 0.01;
		double maxZ = Math.max(pos.zCoord, end.zCoord) + 0.01;
		double minX = Math.min(pos.xCoord, end.xCoord) - 0.01;
		double minY = Math.min(pos.yCoord, end.yCoord) - 0.01;
		double minZ = Math.min(pos.zCoord, end.zCoord) - 0.01;
		this.aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		this.texture = material;
		this.color = tex;
	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		//this.line.readNBT(nbt);
		super.readExtraObjectFromNBT(nbt);
		double maxX = nbt.getDouble("maxX");
		double maxY = nbt.getDouble("maxY");
		double maxZ = nbt.getDouble("maxZ");
		double minX = nbt.getDouble("minX");
		double minY = nbt.getDouble("minY");
		double minZ = nbt.getDouble("minZ");
		this.aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		super.writeExtraObjectToNBT(nbt);
		nbt.setDouble("maxX", aabb.maxX);
		nbt.setDouble("maxY", aabb.maxY);
		nbt.setDouble("maxZ", aabb.maxZ);
		nbt.setDouble("minX", aabb.minX);
		nbt.setDouble("minY", aabb.minY);
		nbt.setDouble("minZ", aabb.minZ);
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB boundingBox) {
		return aabb.intersectsWith(boundingBox);
	}

	/*@Override
	public Vec3d interactWithLine(Vec3d s, Vec3d e) {
		return aabb.calculateIntercept(s, e).hitVec;
	}*/

	@Override
	public Line interactWithRay(Vec3d set, Vec3d end) {
		RayTraceResult v = aabb.calculateIntercept(set, end);
		if (v != null && v.hitVec != null) {
			return new Line(v.hitVec, v.hitVec);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void particle() {
		Vec3d center = new Vec3d((aabb.maxX + aabb.minX) / 2, (aabb.maxY + aabb.minY) / 2, (aabb.maxZ + aabb.minZ) / 2);
		int b0 = (int) ((aabb.maxX - aabb.minX) * 4) + 1;
		int b1 = (int) ((aabb.maxY - aabb.minY) * 4) + 1;
		int b2 = (int) ((aabb.maxZ - aabb.minZ) * 4) + 1;

		for (int i1 = 0; i1 < b0; ++i1) {
			for (int j1 = 0; j1 < b1; ++j1) {
				for (int k1 = 0; k1 < b2; ++k1) {
					double d0 = aabb.minX + ((double) i1) / (double) b0 * (aabb.maxX - aabb.minX);
					double d1 = aabb.minY + ((double) j1) / (double) b1 * (aabb.maxY - aabb.minY);
					double d2 = aabb.minZ + ((double) k1) / (double) b2 * (aabb.maxZ - aabb.minZ);
					Main.proxy.addDiggingEffect(worldObj, center, d0, d1, d2, this.texture, color);
				}
			}
		}
	}

	@Override
	public void dropItem() {

		float f = this.random.nextFloat() * 0.2F + 0.1F;
		float f1 = this.random.nextFloat() * 0.2F + 0.1F;
		float f2 = this.random.nextFloat() * 0.2F + 0.1F;

		ItemStack itemstack1 = new ItemStack(ResisterItem.ItemTofu, 1, this.color);

		NBTTagCompound nbt = itemstack1.getTagCompound();
		itemstack1.setTagCompound(nbt);
		nbt.setBoolean("activated", false);
		nbt.setDouble("setX", 0.0D);
		nbt.setDouble("setY", 0.0D);
		nbt.setDouble("setZ", 0.0D);
		nbt.setBoolean("useFlag", false);
		EntityItem entityitem = new EntityItem(worldObj, (double) ((float) this.pos.xCoord + f), (double) ((float) this.pos.yCoord + f1), (double) ((float) this.pos.zCoord + f2), itemstack1);

		float f3 = 0.05F;
		entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
		entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
		entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
		worldObj.spawnEntityInWorld(entityitem);
	}

	@Override
	public Vec3d getPos() {
		return this.pos;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return aabb;
	}

	@Override
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		if (aabb.intersectsWith(this.aabb)) {
			collidingBoundingBoxes.add(this.aabb);
		}
	}

	@Override
	public RayTraceResult snap(RayTraceResult mop) {
		List<Vec3d> list = Arrays.asList(new Vec3d(aabb.minX, aabb.minY, aabb.minZ),
				new Vec3d(aabb.minX, aabb.maxY, aabb.minZ),
				new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ),
				new Vec3d(aabb.minX, aabb.minY, aabb.maxZ),
				new Vec3d(aabb.maxX, aabb.minY, aabb.minZ),
				new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ),
				new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ),
				new Vec3d(aabb.maxX, aabb.minY, aabb.maxZ));
		mop.hitVec = MyUtil.snapPoints(mop.hitVec, list, 0.4);
		return mop;
	}

	@Override
	public IDrawable getModel() {
		updateModel();
		return model;
	}

	@Override
	public void updateModel() {
		BB_Model ret = new BB_Model(this.aabb.offset(-pos.xCoord, -pos.yCoord, -pos.zCoord));
		model = ret;
	}

	@Override
	public void transform(Mat4 m) {
		aabb = m.transform(aabb);
		super.transform(m);
	}
}
