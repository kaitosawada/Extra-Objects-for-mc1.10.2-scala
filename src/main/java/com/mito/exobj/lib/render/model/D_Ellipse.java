package com.mito.exobj.lib.render.model;

import com.mito.exobj.utilities.MitoMath;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class D_Ellipse extends BB_Polygon {
	Vec3d pos;
	double axisX;
	double axisY;
	Vec3d normalRotation;

	public D_Ellipse(Vec3d p, Vec3d nr, double min, double maj) {
		this.pos = p;
		this.normalRotation = nr;
		this.axisX = min;
		this.axisY = maj;
	}

	@Override
	public BB_Polygon copy() {
		return new D_Ellipse(MitoMath.copyVec3(pos), MitoMath.copyVec3(normalRotation), axisX, axisY);
	}

	@Override
	public List<Vertex> getLine() {
		List<Vertex> ret = new ArrayList<Vertex>();
		int nmax = 20;
		for (int n = 0; n < nmax; n++) {
			double d = (double) n / (double) nmax * 2 * Math.PI;
			ret.add(new Vertex(new Vec3d(0.5 * axisX * Math.cos(d), 0.5 * axisY * Math.sin(d), 0)));
		}
		return ret;
	}
}
