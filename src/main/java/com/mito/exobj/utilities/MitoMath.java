package com.mito.exobj.utilities;

import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.lib.render.model.Mat4;
import com.mito.exobj.lib.render.model.Vertex;
import net.minecraft.util.math.Vec3d;

public final class MitoMath {

	public static Vec3d ratio_vector(Vec3d set, Vec3d end, double r) {
		Vec3d ret = vectorSum(vectorMul(end, r), vectorMul(set, 1 - r));
		return ret;
	}

	public static double[] crossProduct(double[] d, double[] m) {
		double[] ret = {d[1] * m[2] - d[2] * m[1], d[2] * m[0] - d[0] * m[2], d[0] * m[1] - d[1] * m[0]};
		return ret;
	}

	public static double subAbs(double[] v1, double[] v2) {
		double ret = MitoMath.abs(v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2]);
		return ret;
	}

	public static double subAbs(double[] v1, Vec3d v2) {
		double ret = MitoMath.abs(v1[0] - v2.xCoord, v1[1] - v2.yCoord, v1[2] - v2.zCoord);
		return ret;
	}

	public static double subAbs(Vec3d v1, Vec3d v2) {
		double ret = MitoMath.abs(v1.xCoord - v2.xCoord, v1.yCoord - v2.yCoord, v1.zCoord - v2.zCoord);
		return ret;
	}

	public static double subAbs2(Vec3d v1, Vec3d v2) {
		double ret = MitoMath.abs2(v1.xCoord - v2.xCoord, v1.yCoord - v2.yCoord, v1.zCoord - v2.zCoord);
		return ret;
	}

	public static Vec3d vectorMul(Vec3d d, double m) {
		Vec3d ret = new Vec3d(d.xCoord * m, d.yCoord * m, d.zCoord * m);
		return ret;
	}

	public static Vec3d vectorDiv(Vec3d d, double m) {

		if (m == 0) {
			return d;
		}

		Vec3d ret = new Vec3d(d.xCoord / m, d.yCoord / m, d.zCoord / m);
		return ret;
	}

	public static Vec3d vectorSum(Vec3d... d) {
		double x = 0;
		double y = 0;
		double z = 0;

		for (int n = 0; n < d.length; n++) {
			x = x + d[n].xCoord;
			y = y + d[n].yCoord;
			z = z + d[n].zCoord;
		}

		Vec3d ret = new Vec3d(x, y, z);
		return ret;
	}

	public static Vec3d sub_vector(Vec3d d1, Vec3d d2) {
		if (d1 == null || d2 == null) {
			return new Vec3d(0, 0, 0);
		}
		Vec3d ret = new Vec3d(d1.xCoord - d2.xCoord, d1.yCoord - d2.yCoord, d1.zCoord - d2.zCoord);
		return ret;
	}

	public static Vec3d vectorBezier(Vec3d d1, Vec3d d2, Vec3d d3, Vec3d d4, double r) {
		Vec3d ret = vectorSum(vectorMul(d1, Math.pow(r, 3)), vectorMul(d2, 3 * Math.pow(r, 2) * (1 - r)), vectorMul(d3, 3 * Math.pow((1 - r), 2) * r), vectorMul(d4, Math.pow((1 - r), 3)));
		return ret;
	}

	public static Vec3d normalBezier(Vec3d d1, Vec3d d2, Vec3d d3, Vec3d d4, double r) {
		Vec3d ret = vectorSum(vectorMul(d1, 3 * Math.pow(r, 2)), vectorMul(d2, 3 * r * (2 - 3 * r)), vectorMul(d3, 3 * (3 * r - 1) * (r - 1)), vectorMul(d4, -3 * Math.pow((1 - r), 2)));
		return ret.normalize();
	}

	public static Vec3d normalBezier(Vec3d d1, Vec3d d2, Vec3d d3, double r) {
		Vec3d ret = vectorSum(vectorMul(d1, 2 * r), vectorMul(d2, -(4 * r) + 2), vectorMul(d3, 2 * r - 2));
		return ret.normalize();
	}

	public static double abs(double x, double y, double z) {
		double ret = Math.sqrt(x * x + y * y + z * z);

		return ret;
	}

	public static double abs2(double x, double y, double z) {
		double ret = x * x + y * y + z * z;

		return ret;
	}

	public static Vec3d copyVec3(Vec3d v) {
		return new Vec3d(v.xCoord, v.yCoord, v.zCoord);
	}

	public static double abs(Vec3d v) {
		double ret = Math.sqrt(v.xCoord * v.xCoord + v.yCoord * v.yCoord + v.zCoord * v.zCoord);

		return ret;
	}

	public static double abs2(Vec3d v) {
		double ret = Math.pow(v.xCoord, 2) + Math.pow(v.yCoord, 2) + Math.pow(v.zCoord, 2);

		return ret;
	}

	public static Vec3d unitVector(Vec3d v) {
		Vec3d ret = vectorDiv(v, abs(v));

		return ret;
	}

	public static double setLimExp(double x, double max) {
		double ret;

		if (x > 0) {

			ret = max - max * Math.exp(-x / max);

		} else {

			ret = -max + max * Math.exp(x / max);

		}

		return ret;
	}

	public static Vec3d getNearPoint(Vec3d s, Vec3d e, Vec3d p) {
		Vec3d ret;

		double d1 = abs2(sub_vector(s, p));
		double d2 = abs2(sub_vector(e, p));
		double l = abs2(sub_vector(s, e));

		double k = (d1 - d2 + l) / (2 * l);
		k = k >= 1 ? 1 : (k <= 0 ? 0 : k);
		ret = vectorSum(vectorMul(sub_vector(e, s), k), s);

		return ret;
	}

	public static Line getLineNearPoint(Vec3d s, Vec3d e, Vec3d p) {
		Vec3d ret;

		double d1 = abs2(sub_vector(s, p));
		double d2 = abs2(sub_vector(e, p));
		double l = abs2(sub_vector(s, e));

		double k = (d1 - d2 + l) / (2 * l);
		k = k >= 1 ? 1 : (k <= 0 ? 0 : k);
		ret = vectorSum(vectorMul(sub_vector(e, s), k), s);

		return new Line(ret, p);
	}

	public static Line getDistanceLine(Vec3d s1, Vec3d e1, Vec3d s2, Vec3d e2) {

		Vec3d v1 = MitoMath.sub_vector(e1, s1);
		Vec3d v2 = MitoMath.sub_vector(e2, s2);
		double l1 = v1.lengthVector();
		double l2 = v2.lengthVector();
		Vec3d u1 = v1.normalize();
		Vec3d u2 = v2.normalize();
		Vec3d ds = MitoMath.sub_vector(s2, s1);
		double dot = u1.dotProduct(u2);

		double k1 = ds.dotProduct(sub_vector(u1, vectorMul(u2, dot))) / (1 - dot * dot);
		double k2 = ds.dotProduct(sub_vector(vectorMul(u1, dot), u2)) / (1 - dot * dot);

		k1 = k1 < 0 ? 0 : (k1 > l1 ? l1 : k1);
		k2 = k2 < 0 ? 0 : (k2 > l2 ? l2 : k2);

		return new Line(vectorSum(s1, vectorMul(u1, k1)), vectorSum(s2, vectorMul(u2, k2)));
	}

	public static double distancePointPlane(Vec3d plane, Vec3d normal, Vec3d p) {
		return normal.dotProduct(sub_vector(plane, p));
	}

	public static double distancePointPlane(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4) {
		return distancePointPlane(v1, v2.crossProduct(v3).normalize(), v4);
	}

	public static Vec3d getIntersectPlaneLine(Vec3d plane, Vec3d normal, Vec3d s, Vec3d e) {
		double ls = normal.dotProduct(sub_vector(s, plane));
		double le = normal.dotProduct(sub_vector(e, plane));
		double r = ls / (ls + le);
		if (r < 0 || r > 1) {
			return null;
		}
		Vec3d l = sub_vector(e, s);
		Vec3d ret = vectorSum(vectorMul(l, r), s);

		return ret;
	}

	public static boolean onLine(Vec3d p, Vec3d set, Vec3d end) {
		return subAbs2(getNearPoint(set, end, p), p) < 0.001;
	}

	public static Vec3d crossX(Vec3d side1) {
		return new Vec3d(0, side1.zCoord, -side1.yCoord);
	}

	public static Vec3d crossY(Vec3d side1) {
		return new Vec3d(-side1.zCoord, 0, side1.xCoord);
	}

	public static Vec3d crossZ(Vec3d side1) {
		return new Vec3d(side1.yCoord, -side1.xCoord, 0);
	}

	public static Vec3d rot(Vec3d v1, Vec3d v2, double r, double p, double y) {
		return vectorSum(rotY(rotX(rotZ(sub_vector(v2, v1), r), p), y), v1);
	}

	public static Vec3d rot(Vec3d v1, double r, double p, double y) {
		return rotY(rotX(rotZ(v1, r), p), y);
	}

	public static Vec3d rotX(Vec3d v, double t) {
		double rot = t * 2 * Math.PI / 360;
		double d1 = v.yCoord * Math.cos(rot) - v.zCoord * Math.sin(rot);
		double d2 = v.yCoord * Math.sin(rot) + v.zCoord * Math.cos(rot);
		return new Vec3d(v.xCoord, d1, d2);
	}

	public static Vec3d rotY(Vec3d v, double t) {
		double rot = t * 2 * Math.PI / 360;
		double d1 = v.xCoord * Math.cos(rot) + v.zCoord * Math.sin(rot);
		double d2 = -v.xCoord * Math.sin(rot) + v.zCoord * Math.cos(rot);
		return new Vec3d(d1, v.yCoord, d2);
	}

	public static Vec3d rotZ(Vec3d v, double t) {
		double rot = t * 2 * Math.PI / 360;
		double d1 = v.xCoord * Math.cos(rot) - v.yCoord * Math.sin(rot);
		double d2 = v.xCoord * Math.sin(rot) + v.yCoord * Math.cos(rot);
		return new Vec3d(d1, d2, v.zCoord);
	}

	public static double getYaw(Vec3d v) {

		if (v.xCoord == 0 && v.zCoord == 0) {
			return 0;
		}
		double yaw;
		double zabs = v.zCoord;
		yaw = Math.atan(v.xCoord / zabs) / Math.PI * 180;
		if (0 > v.zCoord) {
			if (0 < v.xCoord) {
				yaw = yaw + 180;
			} else {
				yaw = yaw - 180;
			}
		}
		if (Double.isNaN(yaw)) {
			return 0.0;
		}
		return yaw;
	}

	public static double getYaw(Vec3d v1, Vec3d v2) {
		return getYaw(sub_vector(v2, v1));
	}

	public static double getPitch(Vec3d v) {
		double pitch;
		double xzabs = Math.sqrt(Math.pow(v.xCoord, 2) + Math.pow(v.zCoord, 2));
		pitch = -Math.atan(v.yCoord / xzabs) / Math.PI * 180;
		if (Double.isNaN(pitch)) {
			return 0.0;
		}
		return pitch;
	}

	public static double getPitch(Vec3d v1, Vec3d v2) {
		return getPitch(sub_vector(v2, v1));
	}

	public static Vec3d getNormal(Vec3d v1, Vec3d v2, Vec3d v3) {
		return sub_vector(v2, v1).crossProduct(sub_vector(v3, v1)).normalize();
	}

	public static Vec3d getNormal(Vertex rv1, Vertex rv2, Vertex rv3) {
		return getNormal(rv1.pos, rv2.pos, rv3.pos);
	}

	public static Vec3d rot(Vec3d v, double t, Vec3d n) {
		double a = Math.sin(t);
		double b = 1 - Math.cos(t);
		Mat4 r = Mat4.createMat4(0, -n.zCoord, n.yCoord, n.zCoord, 0, -n.xCoord, -n.yCoord, n.xCoord, 0);
		Mat4 m = new Mat4().add(r.copy().mul(a)).add(r.copy().matrixProduct(r.copy()).mul(b));
		return m.transformVec3d(v);
	}

	public static Mat4 getMatScale(double r, Vec3d e) {
		return Mat4.createMat4(
				(r - 1) * e.xCoord * e.xCoord + 1, (r - 1) * e.xCoord * e.yCoord, (r - 1) * e.xCoord * e.zCoord
				, (r - 1) * e.yCoord * e.xCoord, (r - 1) * e.yCoord * e.yCoord + 1, (r - 1) * e.yCoord * e.zCoord
				, (r - 1) * e.zCoord * e.xCoord, (r - 1) * e.zCoord * e.yCoord, (r - 1) * e.zCoord * e.zCoord + 1);
	}

	public static Vec3d getHalfDegreeVec(Vec3d vec, Vec3d vec1, Vec3d vec2) {
		return vec1.subtract(vec).normalize().add(vec2.subtract(vec1).normalize()).normalize();
	}
}
