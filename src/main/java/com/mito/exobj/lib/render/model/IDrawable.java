package com.mito.exobj.lib.render.model;

import java.util.List;

import com.mito.exobj.lib.render.CreateVertexBufferObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;

public interface IDrawable {

	void drawWithTessellator(Vec3d offset, double roll, double pitch, double yaw, double size, float partialTickTime);

	void drawWithVBO(CreateVertexBufferObject c, Vec3d offset, double roll, double pitch, double yaw, double size);

	void drawVBOIIcon(CreateVertexBufferObject c, TextureAtlasSprite iicon, Vec3d pos);

	void drawLine(CreateVertexBufferObject c);

	void writeObj(List<String> vertexs, List<String> vertexs_t, List<String> vertexs_n, List<String> group);

	IDrawable offset(Vec3d sub_vector);

}
