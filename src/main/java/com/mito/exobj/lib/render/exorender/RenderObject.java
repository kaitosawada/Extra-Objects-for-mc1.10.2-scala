package com.mito.exobj.lib.render.exorender;

import com.mito.exobj.lib.render.*;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.ModelObject;
import com.mito.exobj.lib.render.RenderHandler;
import com.mito.exobj.module.main.ClientProxy;
import com.mito.exobj.lib.render.model.IDrawable;
import com.mito.exobj.module.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class RenderObject extends BB_Render {

	@Override
	public void drawHighLight(ExtraObject base, float partialticks) {
		ModelObject brace = (ModelObject) base;

		BraceHighLightHandler data = ((ClientProxy) Main.proxy).bh;
		if (data.key == null || !data.key.equals(base)) {
			data.buffer.delete();
			CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
			c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_LINES)
					.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			IDrawable model = brace.getModel();
			c.translate(brace.getPos());
			if (model != null)
				model.drawLine(c);
			data.key = base;
			VBOHandler vbo = c.end();
			data.buffer.add(vbo);
		}

		RenderHandler.enableClient();
		data.buffer.draw();
		RenderHandler.disableClient();
	}

	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		ModelObject brace = (ModelObject) base;
		//BraceTypeRegistry.getFigure(brace.shape).drawBraceTessellator(brace, partialTickTime);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base, Vec3d pos) {
		ModelObject brace = (ModelObject) base;
		c.setBrightness(base.getBrightnessForRender(0.0f));
		c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		BlockRendererDispatcher blockrender = Minecraft.getMinecraft().getBlockRendererDispatcher();
		TextureAtlasSprite tas = blockrender.getModelForState(brace.texture.getStateFromMeta(brace.color)).getParticleTexture();
		c.pushMatrix();
		IDrawable model = brace.getModel();
		c.translate(brace.getPos());
		c.translate(brace.rand);
		if (model != null)
			model.drawVBOIIcon(c, tas, pos);
		c.popMatrix();
	}
}
