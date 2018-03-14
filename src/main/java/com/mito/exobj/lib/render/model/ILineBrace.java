package com.mito.exobj.lib.render.model;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface ILineBrace extends ITransform {

	void writeNBT(NBTTagCompound nbt);

	RayTraceResult snap(RayTraceResult mop);

	double getLength();

	Vec3d getStart();

	Vec3d getEnd();

	LineWithDirection[] getDrawLine(double size);

	List<Line> getSegments();

	List<Vec3d> getLine();

}
