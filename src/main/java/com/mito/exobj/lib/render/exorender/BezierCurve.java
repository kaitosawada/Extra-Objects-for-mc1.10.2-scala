package com.mito.exobj.lib.render.exorender;

import com.mito.exobj.lib.render.model.*;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BezierCurve implements ILineBrace {

	public Vec3d[] points;

	public BezierCurve(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
		this.points = new Vec3d[]{p1, p2, p3, p4};
	}

	public Vec3d getPoint(double t) {
		Vec3d ret = processBezier(points, t);
		return ret;
	}

	public Vec3d getTangent(double t) {
		if (points.length == 3) {
			return MitoMath.normalBezier(points[2], points[1], points[0], t);
		} else if (points.length == 4) {
			return MitoMath.normalBezier(points[3], points[2], points[1], points[0], t);
		} else if (points.length == 2) {
			return MitoMath.sub_vector(points[1], points[0]).normalize();
		}
		return null;
	}

	public Vec3d secondTan(double t) {
		Vec3d ret = MitoMath.vectorSum(MitoMath.vectorMul(points[0], 1 - t), MitoMath.vectorMul(points[1], 3 * t - 2), MitoMath.vectorMul(points[2], 1 - 3 * t), MitoMath.vectorMul(points[3], t));
		return ret.normalize();
	}

	public Vec3d processBezier(Vec3d[] points, double t) {
		if (points.length > 1) {
			Vec3d[] ps = new Vec3d[points.length - 1];
			for (int n = 0; n < points.length - 1; n++) {
				ps[n] = MitoMath.ratio_vector(points[n], points[n + 1], t);
			}
			return processBezier(ps, t);
		} else if (points.length == 1) {
			return points[0];
		}
		return null;
	}

	private void setVec3(NBTTagCompound nbt, String name, Vec3d vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	@Override
	public void writeNBT(NBTTagCompound nbt) {
		setVec3(nbt, "bezier1", points[0]);
		setVec3(nbt, "bezier2", points[1]);
		setVec3(nbt, "bezier3", points[2]);
		setVec3(nbt, "bezier4", points[3]);
		nbt.setInteger("line", 1);
	}

	@Override
	public void transform(Mat4 m) {
		for (int n = 0; n < points.length; n++) {
			points[n] = m.transformVec3d(points[n]);
		}
	}

	public List<Vec3d> getLine() {
		List<Vec3d> ret = new LinkedList<Vec3d>();
		int nm = this.getAccuracy();
		for (int n = 0; n < nm; n++) {
			Vec3d v = this.getPoint((double) n / (double) nm);
			ret.add(v);
		}
		return ret;
	}

	public List<Line> getSegments() {
		List<Line> ret = new LinkedList<Line>();
		int nm = this.getAccuracy();
		for (int n = 0; n < nm - 1; n++) {
			Vec3d v = this.getPoint((double) n / (double) nm);
			Vec3d v1 = this.getPoint((double) (n + 1) / (double) nm);
			ret.add(new Line(v, v1));
		}
		return ret;
	}


	@Override
	public RayTraceResult snap(RayTraceResult mop) {
		List<Vec3d> list = Arrays.asList(
				getStart(),
				getEnd(),
				getPoint(0.5));
		mop.hitVec = MyUtil.snapPoints(mop.hitVec, list, 1.0);
		return mop;
	}

	public int getAccuracy() {
		//int i = (int) (points[0].distanceTo(points[1]) + points[1].distanceTo(points[2]) + points[2].distanceTo(points[3]) * 5.0);
		return 20;//Math.max(i, 20);
	}

	@Override
	public double getLength() {
		double ret = 0;
		List<Line> list = this.getSegments();
		for (Line line : list) {
			ret += line.getLength();
		}
		return ret;
	}

	@Override
	public Vec3d getStart() {
		return this.points[0];
	}

	@Override
	public Vec3d getEnd() {
		return this.points[3];
	}

	@Override
	public LineWithDirection[] getDrawLine(double size) {
		int nmax = this.getAccuracy();
		LineWithDirection[] ret = new LineWithDirection[nmax];
		Vec3d s = this.getPoint(0);
		Vec3d ds = this.getTangent(0);
		Vec3d dds = this.secondTan(0);
		for (int n = 0; n < nmax; n++) {
			double t1 = (double) (n + 1) / (double) nmax;
			Vec3d e = this.getPoint(t1);
			Vec3d de = this.getTangent(t1);
			Vec3d dde = this.secondTan(t1);
			//MyLogger.info(dds.dotProduct(dde));
			ret[n] = new LineWithDirection(s, e, MyUtil.getRotationMatrix(ds, dds), MyUtil.getRotationMatrix(de, dde));
			s = MitoMath.copyVec3(e);
			ds = MitoMath.copyVec3(de);
			dds = MitoMath.copyVec3(dde);
		}
		return ret;
	}

}
