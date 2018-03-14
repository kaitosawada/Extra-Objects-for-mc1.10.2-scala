package com.mito.exobj.lib.render.model;

import com.mito.exobj.utilities.MitoMath;
import net.minecraft.util.math.Vec3d;

public class Vertex {

	public final Vec3d pos;
	public final double u;
	public final double v;
	public final Vec3d norm;

	public Vertex(double x, double y) {
		this(x, y, 0);
	}

	public Vertex(double x, double y, double z) {
		this(x, y, z, x, y);
	}

	public Vertex(Vec3d p) {
		this(p, p.xCoord, p.yCoord);
	}

	public Vertex(Vec3d p, double u, double v) {
		this(p, u, v, new Vec3d(0, 0, 1));
	}

	public Vertex(double x, double y, double z, double u, double v) {
		this(new Vec3d(x, y, z), u, v);
	}

	public Vertex(double x, double y, double z, double u, double v, Vec3d norm) {
		this(new Vec3d(x, y, z), u, v, norm);
	}

	public Vertex(Vec3d v1, double u, double v, Vec3d norm) {
		this.pos = v1;
		this.u = u;
		this.v = v;
		this.norm = norm;
	}

	public Vertex resize(double size) {
		return new Vertex(MitoMath.vectorMul(this.pos, size), this.u * size + 0.5, this.v * size + 0.5, norm);
	}

	public Vertex addVector(double i, double j, double l) {
		return new Vertex(this.pos.addVector(i, j, l), this.u + i, this.v + j, norm);
	}

	public Vertex addVector(Vec3d v) {
		return this.addVector(v.xCoord, v.yCoord, v.zCoord);
	}

	public Vertex rot(double roll, double pitch, double yaw) {
		return new Vertex(MitoMath.rot(this.pos, roll, pitch, yaw), this.u, this.v, MitoMath.rot(this.norm, roll, pitch, yaw));
	}

	public Vertex copy() {
		return new Vertex(pos, u, v, norm);
	}

	public Vertex reverse() {
		return new Vertex(pos, u, v, MitoMath.vectorMul(norm, -1));
	}

	public Vertex transform(Mat4 mat) {
		return new Vertex(mat.transformVec3d(pos), u, v, mat.transformVec3d(norm).normalize());
	}

}
