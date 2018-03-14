package com.mito.exobj.lib.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.lib.render.CreateVertexBufferObject;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;

public class BB_Polygon implements IDrawBrace, IDrawable {

	private List<Vertex> line = new ArrayList<Vertex>();
	public boolean smoothShading = false;

	public BB_Polygon() {
	}

	public BB_Polygon(Vertex... list) {
		for (int n = 0; n < list.length; n++) {
			this.getLine().add(list[n]);
		}
	}

	public BB_Polygon(List<Vertex> line) {
		this.line = line;
	}

	public BB_Polygon(double... list) {
		for (int n = 0; n < list.length / 3; n++) {
			Vertex v = new Vertex(list[(3 * n)], list[(3 * n + 1)], list[(3 * n + 2)], 0.0D, 0.0D);
			this.getLine().add(v);
		}
	}

	public BB_Polygon(Triangle tri) {
		for (Vertex v : tri.vertexs) {
			getLine().add(v.copy());
		}
	}

	@Override
	public BB_Model getModel(ILineBrace bc , double size, double roll, Vec3d pos) {
		BB_Model ret = new BB_Model();

		LineWithDirection[] ls = bc.getDrawLine(size);
		if (ls.length < 1) {
			return ret;
		}
		BB_Polygon p = this.transform(size, roll);
		Mat4 mat = Mat4.createMat4();
		mat.addCoord(MitoMath.sub_vector(bc.getStart(), pos));
		mat.transMat(ls[0].mat1);
		ret.planes.add(p.reverse().transform(mat));
		mat = Mat4.createMat4();
		mat.addCoord(MitoMath.sub_vector(bc.getEnd(), pos));
		mat.transMat(ls[ls.length - 1].mat2);
		ret.planes.add(p.transform(mat));
		double v = 0.0D;
		for (LineWithDirection lwd : ls) {
			Vec3d s = MitoMath.sub_vector(lwd.start, pos);
			Vec3d e = MitoMath.sub_vector(lwd.end, pos);
			double usum = 0.0D;
			double vofst = MitoMath.subAbs(s, e);
			for (int n1 = 0; n1 < p.line.size(); n1++) {
				Vec3d v1 = p.line.get(n1 == 0 ? p.line.size() - 1 : n1 - 1).pos;
				Vec3d v2 = p.line.get(n1).pos;
				Vec3d vs1 = MitoMath.vectorSum(lwd.mat1.transformNormal(v1), s);
				Vec3d vs2 = MitoMath.vectorSum(lwd.mat1.transformNormal(v2), s);
				Vec3d ven1 = MitoMath.vectorSum(lwd.mat2.transformNormal(v1), e);
				Vec3d ven2 = MitoMath.vectorSum(lwd.mat2.transformNormal(v2), e);
				Vec3d norm = MitoMath.unitVector(new Vec3d(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D));
				Vec3d norm1 = lwd.mat1.transformNormal(norm);
				Vec3d norm2 = lwd.mat2.transformNormal(norm);
				double uofst = MitoMath.subAbs(v1, v2);
				double zofst = v1.zCoord - v2.zCoord;
				Vertex ve1 = new Vertex(vs1, v, usum, norm1);
				Vertex ve2 = new Vertex(vs2, v + zofst, uofst + usum, norm1);
				Vertex ve3 = new Vertex(ven2, v + vofst + zofst, uofst + usum, norm2);
				Vertex ve4 = new Vertex(ven1, v + vofst, usum, norm2);
				BB_Polygon square = new BB_Polygon(ve1, ve2, ve3, ve4);
				ret.planes.add(square);
				usum += uofst;
			}
			v += vofst;
		}
		return ret;
	}

	public BB_Polygon transform(Mat4 mat) {
		Vertex[] ret = new Vertex[this.getLine().size()];
		for (int n = 0; n < getLine().size(); n++) {
			ret[n] = this.getLine().get(n).transform(mat);
		}
		return new BB_Polygon(ret);
	}

	public BB_Polygon transform(double size, double roll) {
		Vertex[] ret = new Vertex[this.getLine().size()];
		Mat4 mat = Mat4.createMat4().rotZ(roll);
		for (int n = 0; n < getLine().size(); n++) {
			ret[n] = this.getLine().get(n).transform(mat).resize(size);
		}
		return new BB_Polygon(ret);
	}

	public BB_Polygon copy() {
		Vertex[] ret = new Vertex[this.getLine().size()];
		for (int n = 0; n < getLine().size(); n++) {
			ret[n] = this.getLine().get(n).copy();
		}
		return new BB_Polygon(ret);
	}

	public BB_Polygon reverse() {
		Vertex[] ret = new Vertex[this.getLine().size()];
		int nmax = getLine().size();
		for (int n = 0; n < nmax; n++) {
			ret[nmax - n - 1] = this.getLine().get(n).reverse();
		}
		return new BB_Polygon(ret);
	}

	public List<Vertex> getLine() {
		return line;
	}

	@Override
	public void drawWithTessellator(Vec3d offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawWithVBO(CreateVertexBufferObject c, Vec3d offset, double roll, double pitch, double yaw, double size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawVBOIIcon(CreateVertexBufferObject c, TextureAtlasSprite iicon, Vec3d pos) {
		List<Triangle> arrayTriangle = MyUtil.decomposeTexture(this);
		//List<Triangle> arrayTriangle = MyUtil.decomposePolygon(this.line);
		for (Triangle triangle : arrayTriangle) {
			triangle.drawIcon(c, iicon, Triangle.EnumFace.OBVERSE, pos);
		}
	}

	@Override
	public void writeObj(List<String> vertexs, List<String> vertexs_t, List<String> vertexs_n, List<String> group) {
	}

	@Override
	public void drawLine(CreateVertexBufferObject c) {
		List<Vertex> l = getLine();
		int nmax = l.size();
		for (int n = 1; n < nmax; n++) {
			c.registVertexWithUV(l.get(n - 1));
			c.registVertexWithUV(l.get(n));
		}
	}

	@Override
	public IDrawable offset(Vec3d vec) {
		return this.transform(Mat4.createMat4().addCoord(vec));
	}

}
