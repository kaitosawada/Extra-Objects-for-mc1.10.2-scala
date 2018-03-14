package com.mito.exobj.lib.render.model;

import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class Arc implements ILineBrace{

	public Vec3d center;
	public Vec3d dia1;
	public Vec3d dia2;
	public double rad;

	public Arc(Vec3d v1, Vec3d v2, Vec3d v3, double d){
		center = v1;
		dia1 = v2;
		dia2 = v3;
		rad = d;
	}

	@Override
	public void writeNBT(NBTTagCompound nbt) {
		setVec3(nbt, "center", center);
		setVec3(nbt, "dia1", dia1);
		setVec3(nbt, "dia2", dia2);
		nbt.setDouble("rad", rad);
		nbt.setInteger("line", 3);
	}

	private void setVec3(NBTTagCompound nbt, String name, Vec3d vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	@Override
	public void transform(Mat4 m) {
		center = m.transformVec3d(center);
		dia1 = m.transformNormal(dia1);
		dia2 = m.transformNormal(dia2);
	}

	@Override
	public RayTraceResult snap(RayTraceResult mop) {
		List<Vec3d> list = Arrays.asList(
				getStart(),
				getPoint(rad / 2),
				getEnd());
		mop.hitVec = MyUtil.snapPoints(mop.hitVec, list, 1.0);
		return mop;
	}

	@Override
	public double getLength() {
		return rad * dia1.lengthVector();
	}

	@Override
	public Vec3d getStart() {
		return center.add(dia1);
	}

	public Vec3d getPoint(double t) {
		return center.add(dia1.scale(Math.sin(t)).add(dia2.scale(Math.sin(t))));
	}

	@Override
	public Vec3d getEnd() {
		return getPoint(rad);
	}

	final int accuracy = 40;

	@Override
	public LineWithDirection[] getDrawLine(double size) {
		return new LineWithDirection[0];
	}

	@Override
	public List<Line> getSegments() {
		return null;
	}

	@Override
	public List<Vec3d> getLine() {

		return null;
	}
}
