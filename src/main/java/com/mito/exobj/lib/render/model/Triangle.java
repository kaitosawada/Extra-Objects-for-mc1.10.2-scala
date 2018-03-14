package com.mito.exobj.lib.render.model;

import com.mito.exobj.lib.render.CreateVertexBufferObject;
import com.mito.exobj.module.main.Main;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Triangle {
	public final Vertex[] vertexs = new Vertex[3];

	public Triangle(Vertex v1, Vertex v2, Vertex v3) {
		this.vertexs[0] = v1;
		this.vertexs[1] = v2;
		this.vertexs[2] = v3;
	}

	public void drawIcon(CreateVertexBufferObject c, TextureAtlasSprite iicon, EnumFace face, Vec3d pos) {
		if (iicon != null) {
			double mu = iicon.getMinU();
			double mv = iicon.getMinV();
			double du = iicon.getMaxU() - iicon.getMinU();
			double dv = iicon.getMaxV() - iicon.getMinV();
			double minu = Math.min(Math.min(vertexs[0].u, vertexs[1].u), vertexs[2].u);
			double minv = Math.min(Math.min(vertexs[0].v, vertexs[1].v), vertexs[2].v);
			double osu = Math.floor(minu + 0.0001);
			double osv = Math.floor(minv + 0.0001);
			Vertex[] va = face.getOrder(vertexs);
			for (Vertex v : va) {
				setBrightness(c, v.pos.add(pos));
				c.setNormal(v.norm);
				c.registVertexWithUV(v.pos, (v.u - osu) * du + mu, mv + (v.v - osv) * dv);
			}
		}
	}

	private void setBrightness(CreateVertexBufferObject c, Vec3d pos) {
		int i = 0;
		if (Main.quality) {
			for(int x : getneighbor(pos.xCoord)){
				for(int y : getneighbor(pos.yCoord)){
					for(int z : getneighbor(pos.zCoord)){
						int i1 = setBrightness(x, y, z);
						if(i1 > i){
							i = i1;
						}
					}
				}
			}
		}
		c.setBrightness(i);
	}

	private int[] getneighbor(double d){
		int i = MathHelper.floor_double(d);
		int i1 = i + ((d - i < 0.5) ? -1 : 1);
		return new int[]{i, i1};
	}

	private int setBrightness(int x, int y, int z) {
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, 0, z);
		if (Main.proxy.getClientWorld().isBlockLoaded(blockPos)) {
			blockPos.setY(y);
			return Main.proxy.getClientWorld().getCombinedLight(blockPos, 0);
		}
		return 0;
	}

	public Triangle transform(Mat4 mat) {
		return new Triangle(this.vertexs[0].transform(mat), this.vertexs[1].transform(mat), this.vertexs[2].transform(mat));
	}

	public Triangle translate(Vec3d start) {
		return new Triangle(this.vertexs[0].addVector(start), this.vertexs[1].addVector(start), this.vertexs[2].addVector(start));

	}

	public Triangle copy() {
		return new Triangle(this.vertexs[0].copy(), this.vertexs[1].copy(), this.vertexs[2].copy());
	}

	public enum EnumFace {
		OBVERSE, REVERSE {
			@Override
			public Vertex[] getOrder(Vertex[] va) {
				return new Vertex[]{va[2], va[1], va[0]};
			}
		},
		BOTH {
			@Override
			public Vertex[] getOrder(Vertex[] va) {
				return new Vertex[]{va[0], va[1], va[2], va[2], va[1], va[0]};
			}
		};

		public Vertex[] getOrder(Vertex[] va) {
			return va;
		}
	}

}
