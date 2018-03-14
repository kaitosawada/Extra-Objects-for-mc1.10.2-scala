package com.mito.exobj.lib.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class LineLoop implements ILineBrace {

	public List<Vec3d> line = new ArrayList<Vec3d>();
	public boolean isLoop = false;

	public LineLoop(Vec3d... list) {
		for (Vec3d v : list) {
			line.add(v);
		}
	}

	private void setVec3d(NBTTagCompound nbt, String name, Vec3d vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	private Vec3d getVec3d(NBTTagCompound nbt, String name) {
		return new Vec3d(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
	}

	@Override
	public void writeNBT(NBTTagCompound nbt) {
		//MyLogger.info("brace read line loop " + line.size());
		NBTTagList taglistGroups = new NBTTagList();
		for (Vec3d v : line) {
			NBTTagCompound nbt1 = new NBTTagCompound();
			setVec3d(nbt1, "vec", v);
			taglistGroups.appendTag(nbt1);
		}
		nbt.setTag("line_list", taglistGroups);
		//MyLogger.info(isLoop + "");

		nbt.setBoolean("isLoop", isLoop);
		nbt.setInteger("line", 2);
	}

	@Override
	public void transform(Mat4 m) {
		List<Vec3d> list = new ArrayList<>();
		for (Vec3d l : line) {
			list.add(m.transformVec3d(l));
		}
		line = list;
	}

	@Override
	public List<Line> getSegments() {
		List<Line> ret = new ArrayList<Line>();
		for (int n = 0; n < line.size() - 1; n++) {
			Vec3d v = MitoMath.copyVec3(getVec(n));
			Vec3d v1 = MitoMath.copyVec3(getVec(n + 1));
			ret.add(new Line(v, v1));
		}
		if (isLoop) {
			ret.add(new Line(getEnd(), getStart()));
		}
		return ret;
	}

	@Override
	public RayTraceResult snap(RayTraceResult mop) {
		List<Line> list = this.getSegments();
		for (Line l : list) {
			if (MitoMath.getLineNearPoint(l.start, l.end, mop.hitVec).getLength() < 0.01) {
				l.snap(mop);
				return mop;
			}
		}
		return mop;
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
		/*if (this.line.isEmpty()) {
			return Vec3d.createVectorHelper(0, 0, 0);
		}*/
		return this.getVec(0);
	}

	@Override
	public Vec3d getEnd() {
		/*if (this.line.isEmpty()) {
			return Vec3d.createVectorHelper(0, 0, 0);
		}*/
		return this.getVec(line.size() - 1);
	}

	private Vec3d getVec(int n) {
		return line.get(Math.floorMod(n, line.size()));
	}

	@Override
	public LineWithDirection[] getDrawLine(double size) {
		if (isLoop) {
			LineWithDirection[] ret = new LineWithDirection[line.size()];
			Vec3d a = getVec(0);
			Vec3d a_dif = MitoMath.getHalfDegreeVec(getVec(-1), a, getVec(1));
			Vec3d a_sec = new Vec3d(0, -1, 0);
			double r = Math.abs(1 / Math.sqrt((1 - a.subtract(getVec(-1)).normalize().dotProduct(a.subtract(getVec(1)).normalize())) / 2));
			Mat4 m1 = MitoMath.getMatScale(r, a_sec).matrixProduct(MyUtil.getRotationMatrix(a_dif));
			for (int n = 1; n < line.size() + 1; n++) {
				Vec3d b = getVec(n);
				Vec3d b_dif = MitoMath.getHalfDegreeVec(a, b, getVec(n + 1));
				Vec3d b_dd = b.subtract(a).normalize().add(b.subtract(getVec(n + 1)).normalize()).normalize();
				Vec3d b_sec = a_sec.crossProduct(b.subtract(a)).crossProduct(b_dif).normalize().scale(-1);
				r = Math.abs(1 / Math.sqrt((1 - b.subtract(getVec(n + 1)).normalize().dotProduct(b.subtract(a).normalize())) / 2));
				Mat4 m2 = n == line.size() ? MitoMath.getMatScale(r, b_dd).matrixProduct(MyUtil.getRotationMatrix(a_dif)) : MitoMath.getMatScale(r, b_dd).matrixProduct(MyUtil.getRotationMatrix(b_dif, b_sec));
				ret[n - 1] = new LineWithDirection(a, b, m1, m2);
				a = b;
				a_sec = b_sec;
				m1 = m2;
			}
			return ret;
		} else {
			LineWithDirection[] ret = new LineWithDirection[line.size() - 1];
			Vec3d a = getVec(0);
			Vec3d a_dif = getVec(1).subtract(getVec(0));
			Vec3d a_sec = new Vec3d(0, -1, 0);
			Mat4 m1 = MyUtil.getRotationMatrix(a_dif, a_sec);
			Mat4 m2 = new Mat4();
			for (int n = 1; n < line.size() - 1; n++) {
				Vec3d b = getVec(n);
				Vec3d b_dif = getVec(n).subtract(getVec(n - 1)).normalize().add(getVec(n + 1).subtract(getVec(n)).normalize()).normalize();
				Vec3d b_dd = b.subtract(a).normalize().add(b.subtract(getVec(n + 1)).normalize()).normalize();
				Vec3d b_sec = a_sec.crossProduct(b.subtract(a)).crossProduct(b_dif).normalize().scale(-1);
				double r = Math.abs(1 / Math.sqrt((1 - b.subtract(getVec(n + 1)).normalize().dotProduct(b.subtract(a).normalize())) / 2));
				Mat4 m3 = MitoMath.getMatScale(r, b_dd);
				/*MyLogger.info(m3.toString());
				MyLogger.info(b_sec.toString());*/
				m2 = m3.matrixProduct(MyUtil.getRotationMatrix(b_dif, b_sec));
				ret[n - 1] = new LineWithDirection(a, b, m1, m2);
				a = b;
				a_sec = b_sec;
				m1 = m2;
			}
			int n1 = line.size() - 1;
			Vec3d e = getVec(n1);
			Vec3d en = getVec(n1).subtract(getVec(n1 - 1));
			ret[n1 - 1] = new LineWithDirection(a, e, m2, MyUtil.getRotationMatrix(en, a_sec));
			return ret;
		}
		/*int acc = 2;
		List<Line> list = this.getSegments();
		int num = acc * (list.size() - 1) + list.size();
		LineWithDirection[] ret = new LineWithDirection[num];
		int num1 = 0;
		for (Line l : list) {
			ret[num1] = l.getODrawLine();
			num1 = num1 + acc + 1;
		}
		for (int n = 0; n < list.size() - 1; n++) {
			Line l1 = list.get(n);
			Line l2 = list.get(n + 1);
			Vec3d s = l1.getEnd();
			Vec3d sn = l1.getDirection();
			Vec3d en = l2.getDirection();
			for (int n1 = 0; n1 < acc; n1++) {
				double t = (double) n / (double) acc;
				double t1 = (double) (n + 1) / (double) acc;
				Vec3d sn1 = MitoMath.ratio_vector(sn, en, t).normalize();
				Vec3d en1 = MitoMath.ratio_vector(sn, en, t1).normalize();
				Mat4 m1 = MyUtil.getRotationMatrix(sn1);
				Mat4 m2 = MyUtil.getRotationMatrix(en1);
				//MyLogger.info("line loop " + (n * 6 + n1 + 1));
				ret[n * (acc + 1) + n1 + 1] = new LineWithDirection(s, s, m1, m2);
			}
		}
		return ret;*/
	}

	//should consider on isLoop
	@Override
	public List<Vec3d> getLine() {
		return line;
	}

}
