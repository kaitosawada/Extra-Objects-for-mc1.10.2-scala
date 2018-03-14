package com.mito.exobj.lib.render.model;

import com.mito.exobj.lib.render.CreateVertexBufferObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BB_ModelGroup extends BB_Model {

	public List<IDrawable> models = new ArrayList<IDrawable>();

	public BB_ModelGroup(IDrawable... list) {
		for (IDrawable m : list) {
			models.add(m);
		}
	}

	public void drawWithTessellator(Vec3d offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		for (IDrawable m : models) {
			m.drawWithTessellator(offset, roll, pitch, yaw, size, partialTickTime);
		}
	}

	public void drawWithVBO(CreateVertexBufferObject c, Vec3d offset, double roll, double pitch, double yaw, double size) {
		for (IDrawable m : models) {
			m.drawWithVBO(c, offset, roll, pitch, yaw, size);
		}
	}

	@Override
	public void drawVBOIIcon(CreateVertexBufferObject c, TextureAtlasSprite iicon, Vec3d pos) {
		for (IDrawable m : models) {
			m.drawVBOIIcon(c, iicon, pos);
		}
	}

	public void add(IDrawable model) {
		models.add(model);
	}

	@Override
	public void writeObj(List<String> vertexs, List<String> vertexs_t, List<String> vertexs_n, List<String> group) {
		for (IDrawable m : models) {
			m.writeObj(vertexs, vertexs_t, vertexs_n, group);
		}
	}

	@Override
	public void drawLine(CreateVertexBufferObject c) {
		for (IDrawable m : models) {
			m.drawLine(c);
		}
	}

	@Override
	public IDrawable offset(Vec3d vec) {
		BB_ModelGroup ret = new BB_ModelGroup();
		for (IDrawable p : this.models) {
			ret.models.add(p.offset(vec));
		}
		return ret;
	}

}
