package com.mito.exobj.lib.render.model;

import com.mito.exobj.lib.data.BB_OutputHandler;
import com.mito.exobj.lib.render.CreateVertexBufferObject;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BB_Model implements IDrawable {

	public List<BB_Polygon> planes = new ArrayList<BB_Polygon>();

	public BB_Model(BB_Polygon... list) {
		for (BB_Polygon poly : list)
			planes.add(poly);
	}

	public BB_Model(AxisAlignedBB aabb) {
		double maxX = aabb.maxX;
		double maxY = aabb.maxY;
		double maxZ = aabb.maxZ;
		double minX = aabb.minX;
		double minY = aabb.minY;
		double minZ = aabb.minZ;
		Vec3d n1 = new Vec3d(0, 0, 1);
		Vec3d n2 = new Vec3d(0, 0, -1);
		Vec3d n3 = new Vec3d(0, 1, 0);
		Vec3d n4 = new Vec3d(0, -1, 0);
		Vec3d n5 = new Vec3d(1, 0, 0);
		Vec3d n6 = new Vec3d(-1, 0, 0);
		planes.add(new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxX, maxY, n1), new Vertex(minX, maxY, maxZ, minX, maxY, n1), new Vertex(minX, minY, maxZ, minX, minY, n1), new Vertex(maxX, minY, maxZ, maxX, minY, n1)));
		planes.add(new BB_Polygon(new Vertex(maxX, maxY, minZ, minX, maxY, n2), new Vertex(maxX, minY, minZ, minX, minY, n2), new Vertex(minX, minY, minZ, maxX, minY, n2), new Vertex(minX, maxY, minZ, maxX, maxY, n2)));
		planes.add(new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxZ, maxX, n3), new Vertex(maxX, maxY, minZ, minZ, maxX, n3), new Vertex(minX, maxY, minZ, minZ, minX, n3), new Vertex(minX, maxY, maxZ, maxZ, minX, n3)));
		planes.add(new BB_Polygon(new Vertex(maxX, minY, maxZ, minZ, maxX, n4), new Vertex(minX, minY, maxZ, minZ, minX, n4), new Vertex(minX, minY, minZ, maxZ, minX, n4), new Vertex(maxX, minY, minZ, maxZ, maxX, n4)));
		planes.add(new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxY, maxZ, n5), new Vertex(maxX, minY, maxZ, minY, maxZ, n5), new Vertex(maxX, minY, minZ, minY, minZ, n5), new Vertex(maxX, maxY, minZ, maxY, minZ, n5)));
		planes.add(new BB_Polygon(new Vertex(minX, maxY, maxZ, minY, maxZ, n6), new Vertex(minX, maxY, minZ, minY, minZ, n6), new Vertex(minX, minY, minZ, maxY, minZ, n6), new Vertex(minX, minY, maxZ, maxY, maxZ, n6)));
	}

	public void drawWithTessellator(Vec3d offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
	}

	public void drawWithVBO(CreateVertexBufferObject c, Vec3d offset, double roll, double pitch, double yaw, double size) {
	}

	public void drawVBOIIcon(CreateVertexBufferObject c, TextureAtlasSprite iicon, Vec3d pos) {
		for (BB_Polygon p : this.planes) {
			p.drawVBOIIcon(c, iicon, pos);
		}
	}

	@Override
	public void writeObj(List<String> vertexs, List<String> vertexs_t, List<String> vertexs_n, List<String> group) {
		BB_OutputHandler.groupNum++;
		group.add("s off");
		group.add("g model" + (BB_OutputHandler.groupNum++));
		group.add("usemtl default");
		group.add("s 1");
		for (BB_Polygon p : this.planes) {
			int i = vertexs.size();
			String poly = "f";
			for (Vertex v : p.getLine()) {
				i++;
				vertexs.add("v " + v.pos.xCoord + " " + v.pos.yCoord + " " + v.pos.zCoord);
				vertexs_t.add("vt " + v.u + " " + v.v);
				vertexs_n.add("vn " + v.norm.xCoord + " " + v.norm.yCoord + " " + v.norm.zCoord);
				poly = poly + " " + i + "/" + i + "/" + i;
			}
			group.add(poly);
		}

	}

	@Override
	public void drawLine(CreateVertexBufferObject c) {
		for (BB_Polygon p : this.planes) {
			p.drawLine(c);
		}
	}

	@Override
	public IDrawable offset(Vec3d vec) {
		BB_Model ret = new BB_Model();
		for (BB_Polygon p : this.planes) {
			ret.planes.add((BB_Polygon) p.offset(vec));
		}
		return ret;
	}

}