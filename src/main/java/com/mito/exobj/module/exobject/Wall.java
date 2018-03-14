package com.mito.exobj.module.exobject;

import com.mito.exobj.lib.ModelObject;
import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import com.mito.exobj.lib.render.exorender.IJoint;
import com.mito.exobj.lib.render.model.*;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.utilities.MyUtil;
import javafx.scene.chart.Axis;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wall extends ModelObject {

	public Line line;
	public double size;
	public double height;
	public IDrawBrace db;

	public Wall(World world) {
		super(world);
	}

	public Wall(World world, Vec3d pos) {
		super(world, pos);
	}

	public Wall(World world, Vec3d pos, Vec3d end, Block material, int tex, double size) {
		this(world, pos);
		Vec3d v = new Vec3d(pos.xCoord, (pos.yCoord + end.yCoord) / 2, pos.zCoord);
		Vec3d v1 = new Vec3d(end.xCoord, (pos.yCoord + end.yCoord) / 2, end.zCoord);
		this.height = Math.abs(pos.yCoord - end.yCoord);
		if (height < 0.001) {
			height = 2.0;
		}
		this.db = BraceTypeRegistry.createSquare(size, height, 0, 0);
		this.line = new Line(v, v1);
		this.texture = material;
		this.color = tex;
		this.size = size;
	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		super.readExtraObjectFromNBT(nbt);
		Vec3d start = getVec3(nbt, "start");
		Vec3d end = getVec3(nbt, "end");
		line = new Line(start, end);
		this.size = nbt.getDouble("size");
		this.height = nbt.getDouble("height");
		if (height < 0.001) {
			height = 2.0;
		}
		this.db = BraceTypeRegistry.createSquare(size, height, 0, 0);
	}

	Vec3d getVec3(NBTTagCompound nbt, String name) {
		return new Vec3d(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		if (line != null) {
			line.writeNBT(nbt);
			nbt.setDouble("size", this.size);
			nbt.setDouble("height", this.height);
			super.writeExtraObjectToNBT(nbt);
		}
	}

	@Override
	public IDrawable getModel() {
		updateModel();
		return model;
	}

	@Override
	public void updateModel() {
		model = db.getModel(line, 1.0, 0, pos);
	}

	@Override
	public AxisAlignedBB getBoundingBox(){
		if (line == null) {
			return null;
		}
		double maxX = Math.max(line.start.xCoord, line.end.xCoord) + size;
		double maxY = Math.max(line.start.yCoord, line.end.yCoord) + height / 2 + size;
		double maxZ = Math.max(line.start.zCoord, line.end.zCoord) + size;
		double minX = Math.min(line.start.xCoord, line.end.xCoord) - size;
		double minY = Math.min(line.start.yCoord, line.end.yCoord) - height / 2 - size;
		double minZ = Math.min(line.start.zCoord, line.end.zCoord) - size;
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB aabb) {
		if (line == null) {
			return false;
		}
		double maxX = Math.max(line.start.xCoord, line.end.xCoord) + size;
		double maxY = Math.max(line.start.yCoord, line.end.yCoord) + height / 2 + size;
		double maxZ = Math.max(line.start.zCoord, line.end.zCoord) + size;
		double minX = Math.min(line.start.xCoord, line.end.xCoord) - size;
		double minY = Math.min(line.start.yCoord, line.end.yCoord) - height / 2 - size;
		double minZ = Math.min(line.start.zCoord, line.end.zCoord) - size;
		AxisAlignedBB aabb1 = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		return aabb.intersectsWith(aabb1);
	}

	@Override
	public Line interactWithRay(Vec3d set, Vec3d end) {
		Vec3d ab = end.subtract(set);
		Vec3d se = line.end.subtract(line.start);
		Vec3d normal = new Vec3d(se.zCoord, 0, -se.xCoord).normalize();
		double d = line.start.subtract(set).dotProduct(normal) / ab.dotProduct(normal);
		Vec3d x = set.add(ab.scale(d));
		double maxX = Math.max(line.start.xCoord, line.end.xCoord) + 0.01;
		double maxY = Math.max(line.start.yCoord, line.end.yCoord) + height / 2;
		double maxZ = Math.max(line.start.zCoord, line.end.zCoord) + 0.01;
		double minX = Math.min(line.start.xCoord, line.end.xCoord) - 0.01;
		double minY = Math.min(line.start.yCoord, line.end.yCoord) - height / 2;
		double minZ = Math.min(line.start.zCoord, line.end.zCoord) - 0.01;
		AxisAlignedBB aabb1 = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		if (aabb1.isVecInside(x) && d > 0 && d < 1) {
			return new Line(x, x);
		} else {
			return null;
		}
	}

	@Override
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		if (line == null) {
			return;
		}

		Vec3d v3 = MitoMath.sub_vector(line.end, line.start);
		int div = size > 0 ? (int) Math.floor(MitoMath.abs(v3) / size) + 1 : 1;
		Vec3d part = MitoMath.vectorDiv(MitoMath.sub_vector(v3, MitoMath.vectorMul(v3.normalize(), size)), div);
		Vec3d offset = MitoMath.vectorMul(v3.normalize(), size / 2);
		for (int n = 0; n <= div; n++) {
			Vec3d v = MitoMath.vectorSum(line.start, offset, MitoMath.vectorMul(part, (double) n));
			AxisAlignedBB aabb1 = MyUtil.createAabb(v, size, height, size);
			if (aabb1 != null && aabb1.intersectsWith(aabb)) {
				//list.add(aabb1);
				collidingBoundingBoxes.add(aabb1);
			}
		}
	}

	@Override
	public RayTraceResult snap(RayTraceResult mop) {
		List<Vec3d> list = Arrays.asList(
				new Vec3d(line.start.xCoord, line.start.yCoord + height / 2, line.start.zCoord),
				new Vec3d(line.end.xCoord, line.end.yCoord + height / 2, line.end.zCoord),
				MitoMath.ratio_vector(line.start, line.end, 0.5),
				new Vec3d(line.start.xCoord, line.start.yCoord - height / 2, line.start.zCoord),
				new Vec3d(line.end.xCoord, line.end.yCoord - height / 2, line.end.zCoord));
		mop.hitVec = MyUtil.snapPoints(mop.hitVec, list, 0.4);
		return mop;
	}

	@Override
	public void transform(Mat4 m) {
		this.line.transform(m);
		super.transform(m);
	}

	@Override
	public void scale(Vec3d cent, double i) {
		this.size = size * i;
		this.height = height * i;
		this.db = BraceTypeRegistry.createSquare(size, height, 0, 0);
		super.scale(cent, i);
	}
	@Override
	public void rotate(Vec3d cent, double rol, double pit, double yaw){
	}
}
