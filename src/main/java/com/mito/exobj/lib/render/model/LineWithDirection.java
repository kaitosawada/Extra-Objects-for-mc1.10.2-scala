package com.mito.exobj.lib.render.model;


import net.minecraft.util.math.Vec3d;

public class LineWithDirection {

	public final Vec3d start;
	public final Vec3d end;
	public final Mat4 mat1;
	public final Mat4 mat2;

	public LineWithDirection(Vec3d s, Vec3d e, Mat4 m1, Mat4 m2) {
		start = s;
		end = e;
		mat1 = m1;
		mat2 = m2;
	}

}
