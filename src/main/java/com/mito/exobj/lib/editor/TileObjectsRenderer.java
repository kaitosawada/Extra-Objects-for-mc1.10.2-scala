package com.mito.exobj.lib.editor;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.render.BB_Render;
import com.mito.exobj.lib.render.CreateVertexBufferObject;
import com.mito.exobj.lib.render.RenderHandler;
import com.mito.exobj.lib.render.VBOHandler;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.lib.editor.TileObjects;

import net.minecraft.block.BlockFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class TileObjectsRenderer extends TileEntitySpecialRenderer<TileObjects> {

	@Override
	public void renderTileEntityAt(TileObjects te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.list != null) {
			GL11.glCullFace(GL11.GL_FRONT);
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			//GL11.glTranslated(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

			if (!te.list.isEmpty()) {
				GL11.glPushMatrix();
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				RenderHandler.enableClient();

				Minecraft.getMinecraft().entityRenderer.enableLightmap();
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glShadeModel(GL11.GL_SMOOTH);
				GL11.glPushMatrix();
				te.buffer.draw();
				GL11.glPopMatrix();
				GL11.glShadeModel(GL11.GL_FLAT);

				RenderHandler.disableClient();
				if (te.isShouldUpdateRender()) {
					te.buffer.delete();
					CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
					c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
					c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
					//Vec3d v = new Vec3d(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
					//c.translate(v);
					//MyLogger.info("a2 " + te.list.size());
					for (ExtraObject base : te.list) {
						BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
						render.updateRender(c, base, base.getPos().add(new Vec3d(te.getPos().getX(), te.getPos().getZ(), te.getPos().getY())));
						MyLogger.info("a3 " + render.toString());
					}
					te.setShouldUpdateRender(false);
					VBOHandler vbo = c.end();
					//te.buffer.v = v;
					te.buffer.add(vbo);
					MyLogger.info("render update");
				}
				Minecraft.getMinecraft().entityRenderer.disableLightmap();
				GL11.glPopMatrix();
			}

			GL11.glCullFace(GL11.GL_BACK);
			GL11.glPopMatrix();

		}
	}
}
