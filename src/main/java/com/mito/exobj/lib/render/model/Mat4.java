package com.mito.exobj.lib.render.model;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;

import javax.vecmath.Matrix4d;
import java.nio.DoubleBuffer;


public class Mat4 {

	public double[] val;

	public Mat4() {
		this.val = new double[]{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1,
		};
	}

	public Mat4(double[] val) {
		this.val = val;
	}

	@Deprecated
	public Mat4(Vec3d v1, Vec3d v2, Vec3d norm) {
		// TODO Auto-generated constructor stub
	}

	public static Mat4 createMat4(double... da) {
		if (da.length == 16) {
			return new Mat4(da);
		} else if (da.length == 9) {
			return new Mat4(new double[]{
					da[0], da[1], da[2], 0,
					da[3], da[4], da[5], 0,
					da[6], da[7], da[8], 0,
					0, 0, 0, 1,
			});
		} else if (da.length == 12) {
			return new Mat4(new double[]{
					da[0], da[1], da[2], da[3],
					da[4], da[5], da[6], da[7],
					da[8], da[9], da[10], da[11],
					0, 0, 0, 1,
			});
		} else {
			return new Mat4();
		}
	}

	public Mat4 matrixProduct(Mat4 mat) {
		double[] a1 = this.val;
		double[] a2 = mat.val;
		if (a1.length != 16 || a2.length != 16) {
			return null;
		}
		return createMat4(
				a1[0] * a2[0] + a1[1] * a2[4] + a1[2] * a2[8] + a1[3] * a2[12],
				a1[0] * a2[1] + a1[1] * a2[5] + a1[2] * a2[9] + a1[3] * a2[13],
				a1[0] * a2[2] + a1[1] * a2[6] + a1[2] * a2[10] + a1[3] * a2[14],
				a1[0] * a2[3] + a1[1] * a2[7] + a1[2] * a2[11] + a1[3] * a2[15],

				a1[4] * a2[0] + a1[5] * a2[4] + a1[6] * a2[8] + a1[7] * a2[12],
				a1[4] * a2[1] + a1[5] * a2[5] + a1[6] * a2[9] + a1[7] * a2[13],
				a1[4] * a2[2] + a1[5] * a2[6] + a1[6] * a2[10] + a1[7] * a2[14],
				a1[4] * a2[3] + a1[5] * a2[7] + a1[6] * a2[11] + a1[7] * a2[15],

				a1[8] * a2[0] + a1[9] * a2[4] + a1[10] * a2[8] + a1[11] * a2[12],
				a1[8] * a2[1] + a1[9] * a2[5] + a1[10] * a2[9] + a1[11] * a2[13],
				a1[8] * a2[2] + a1[9] * a2[6] + a1[10] * a2[10] + a1[11] * a2[14],
				a1[8] * a2[3] + a1[9] * a2[7] + a1[10] * a2[11] + a1[11] * a2[15],
				0, 0, 0, 1);
	}

	public void transMat(Mat4 mat) {
		double[] a1 = this.val.clone();
		double[] a2 = mat.val;
		if (a1.length != 16 || a2.length != 16) {
			return;
		}
		val[0] = a1[0] * a2[0] + a1[1] * a2[4] + a1[2] * a2[8];
		val[1] = a1[0] * a2[1] + a1[1] * a2[5] + a1[2] * a2[9];
		val[2] = a1[0] * a2[2] + a1[1] * a2[6] + a1[2] * a2[10];
		val[3] = a1[0] * a2[3] + a1[1] * a2[7] + a1[2] * a2[11] + a1[3];

		val[4] = a1[4] * a2[0] + a1[5] * a2[4] + a1[6] * a2[8];
		val[5] = a1[4] * a2[1] + a1[5] * a2[5] + a1[6] * a2[9];
		val[6] = a1[4] * a2[2] + a1[5] * a2[6] + a1[6] * a2[10];
		val[7] = a1[4] * a2[3] + a1[5] * a2[7] + a1[6] * a2[11] + a1[7];

		val[8] = a1[8] * a2[0] + a1[9] * a2[4] + a1[10] * a2[8];
		val[9] = a1[8] * a2[1] + a1[9] * a2[5] + a1[10] * a2[9];
		val[10] = a1[8] * a2[2] + a1[9] * a2[6] + a1[10] * a2[10];
		val[11] = a1[8] * a2[3] + a1[9] * a2[7] + a1[10] * a2[11] + a1[11];

		val[12] = 0;
		val[13] = 0;
		val[14] = 0;
		val[15] = 1;
	}

	public Vec3d transformVec3d(Vec3d vec) {
		double[] a1 = this.val;
		return new Vec3d(
				a1[0] * vec.xCoord + a1[1] * vec.yCoord + a1[2] * vec.zCoord + a1[3],
				a1[4] * vec.xCoord + a1[5] * vec.yCoord + a1[6] * vec.zCoord + a1[7],
				a1[8] * vec.xCoord + a1[9] * vec.yCoord + a1[10] * vec.zCoord + a1[11]);
	}

	public Vec3d transformNormal(Vec3d vec) {
		double[] a1 = this.val;
		return new Vec3d(
				a1[0] * vec.xCoord + a1[1] * vec.yCoord + a1[2] * vec.zCoord,
				a1[4] * vec.xCoord + a1[5] * vec.yCoord + a1[6] * vec.zCoord,
				a1[8] * vec.xCoord + a1[9] * vec.yCoord + a1[10] * vec.zCoord);
	}

	public Mat4 transposition() {
		return createMat4(val[0], val[4], val[8], val[12], val[1], val[5], val[9], val[13], val[2], val[6], val[10], val[14], val[3], val[7], val[11], val[15]);
	}

	public Mat4 addCoord(Vec3d vec) {
		if (val.length != 16) {
			return null;
		} else {
			val[3] += vec.xCoord;
			val[7] += vec.yCoord;
			val[11] += vec.zCoord;
		}
		return this;
	}

	public Mat4 add(Mat4 mat) {
		if (val.length != 16) {
			return null;
		} else {
			for (int n = 0; n < 16; n++) {
				val[n] += mat.val[n];
			}
		}
		return this;
	}

	public Mat4 mul(double d) {
		if (val.length != 16) {
			return null;
		} else {
			for (int n = 0; n < 16; n++) {
				val[n] *= d;
			}
		}
		return this;
	}

	public Mat4 copy() {
		return createMat4(val[0], val[1], val[2], val[3], val[4], val[5], val[6], val[7], val[8], val[9], val[10], val[11], val[12], val[13], val[14], val[15]);
	}

	public static Mat4 createMat4(Vec3d v1, Vec3d v2, Vec3d v3) {
		return createMat4(v1.xCoord, v2.xCoord, v3.xCoord, v1.yCoord, v2.yCoord, v3.yCoord, v1.zCoord, v2.zCoord, v3.zCoord);
	}

	public static Mat4 createRot(double r, double p, double y) {
		double phi = r * 2 * Math.PI / 360;
		double theta = p * 2 * Math.PI / 360;
		double psi = y * 2 * Math.PI / 360;
		Mat4 ro = createMat4(Math.cos(theta) * Math.cos(phi), Math.cos(phi) * Math.sin(theta) * Math.sin(psi) - Math.sin(phi) * Math.cos(psi), Math.cos(phi) * Math.sin(theta) * Math.cos(psi) + Math.sin(phi) * Math.sin(psi),
				Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta) * Math.sin(psi) + Math.cos(phi) * Math.cos(psi), Math.sin(phi) * Math.sin(theta) * Math.cos(psi) - Math.cos(phi) * Math.sin(psi),
				-Math.sin(theta), Math.cos(theta) * Math.sin(psi), Math.cos(theta) * Math.cos(psi));
		ro.init();
		return ro;
	}

	public Mat4 rpyRotation(double r, double p, double y) {
		Mat4 ro = createRot(r, p, y);
		this.transMat(ro);
		return this;
	}

	public static Mat4 createVec3(Vec3d v) {
		return createMat4(1, 0, 0, v.xCoord, 0, 1, 0, v.yCoord, 0, 0, 1, v.zCoord);
	}

	public Mat4 translate(double x, double y, double z) {
		Mat4 tr = createMat4(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z);

		this.transMat(tr);
		return this;
	}

	public Mat4 translate(Vec3d v) {
		translate(v.xCoord, v.yCoord, v.zCoord);
		return this;
	}


	public static Mat4 createScale(double x, double y, double z) {
		return createMat4(x, 0, 0, 0, y, 0, 0, 0, z);
	}

	public Mat4 scale(double x, double y, double z) {
		Mat4 tr = createMat4(x, 0, 0, 0, y, 0, 0, 0, z);

		this.transMat(tr);
		return this;
	}

	public Mat4 rotate(double a, double x, double y, double z) {
		double angle = a * 2 * Math.PI / 360;
		Mat4 r = createMat4(0, -z, y, z, 0, -x, -y, x, 0);
		Mat4 r2 = r.matrixProduct(r);
		Mat4 ro = createMat4().add(r.mul(Math.sin(angle))).add(r2.mul(1 - Math.cos(angle)));
		ro.init();

		this.transMat(ro);
		return this;
	}

	void init() {
		val[15] = 1;
		val[14] = 0;
		val[13] = 0;
		val[12] = 0;
	}

	public void rotate(double a, Vec3d v) {
		rotate(a, v.xCoord, v.yCoord, v.zCoord);
	}

	public DoubleBuffer getBuffer() {
		DoubleBuffer vertexBuffer = BufferUtils.createDoubleBuffer(16);
		vertexBuffer.put(this.val);
		vertexBuffer.flip();
		return null;
	}

	public Mat4 rotZ(double roll) {
		double phi = roll * 2 * Math.PI / 360;
		Mat4 ro = createMat4(Math.cos(phi), -Math.sin(phi), 0,
				Math.sin(phi), Math.cos(phi), 0,
				0, 0, 1);
		ro.init();
		return this.matrixProduct(ro);
	}

	public Mat4 scale(double size) {
		return scale(size, size, size);
	}

	public AxisAlignedBB transform(AxisAlignedBB aabb) {
		return new AxisAlignedBB(val[0] * aabb.minX + val[1] * aabb.minY + val[2] * aabb.minZ + val[3],
				val[4] * aabb.minX + val[5] * aabb.minY + val[6] * aabb.minZ + val[7],
				val[8] * aabb.minX + val[9] * aabb.minY + val[10] * aabb.minZ + val[11],
				val[0] * aabb.maxX + val[1] * aabb.maxY + val[2] * aabb.maxZ + val[3],
				val[4] * aabb.maxX + val[5] * aabb.maxY + val[6] * aabb.maxZ + val[7],
				val[8] * aabb.maxX + val[9] * aabb.maxY + val[10] * aabb.maxZ + val[11]);
	}

	public String toString() {
		return this.val[0] + ", " + this.val[1] + ", " + this.val[2] + "\n" +
				this.val[4] + ", " + this.val[5] + ", " + this.val[6] + "\n" +
				this.val[8] + ", " + this.val[9] + ", " + this.val[10] + "\n";
	}
}
