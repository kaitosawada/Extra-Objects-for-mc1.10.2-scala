package com.mito.exobj.lib.render;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.data.BB_DataChunk;
import com.mito.exobj.lib.data.BB_DataWorld;
import com.mito.exobj.lib.data.LoadClientWorldHandler;
import com.mito.exobj.module.main.IEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.List;

public class RenderHandler {

	public RenderHandler() {
	}

	public static void enableClient() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public static void disableClient() {
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public static void onRenderEntities(Entity entity, ICamera camera, float partialticks) {
		//MyLogger.info(String.format("[renderpass : %d]", MinecraftForgeClient.getRenderPass()));
		if (MinecraftForgeClient.getRenderPass() == 0) {

			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);

			double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialticks;
			double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialticks;
			double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialticks;

			BB_DataWorld data = LoadClientWorldHandler.INSTANCE.data;

			for (BB_DataChunk chunk : data.coordToDataMapping.values()) {
				List<ExtraObject> list = chunk.braceList;

				if (!list.isEmpty()) {
					GL11.glPushMatrix();
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					enableClient();

					Minecraft.getMinecraft().entityRenderer.enableLightmap();
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
					GL11.glShadeModel(GL11.GL_SMOOTH);
					GL11.glPushMatrix();
					GL11.glTranslatef((float) (-d3 - chunk.buffer.v.xCoord), (float) (-d4 - chunk.buffer.v.yCoord), (float) (-d5 - chunk.buffer.v.zCoord));
					chunk.buffer.draw();
					GL11.glPopMatrix();
					GL11.glShadeModel(GL11.GL_FLAT);

					/*GL11.glPushMatrix();
					for (ExtraObject base : list) {
						BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
						if (!render.isVbo(base)) {
							render.doRender(base, -(float) RenderManager.renderPosX, -(float) RenderManager.renderPosY, -(float) RenderManager.renderPosZ, partialticks);
						}
					}
					GL11.glPopMatrix();*/

					disableClient();
					if (chunk.isShouldUpdateRender()) {
						chunk.buffer.delete();
						CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
						ExtraObject base1 = list.get(0);
						Vec3d v = new Vec3d(-chunk.xPosition * 16.0, -base1.getPos().yCoord, -chunk.zPosition * 16.0);
						c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
						c.translate(v);
						c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
						//MyLogger.info("a2 " + list.size());
						for (ExtraObject base : list) {
							BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
							render.updateRender(c, base, base.getPos());
							//MyLogger.info("a3 " + render.toString());
						}
						chunk.setShouldUpdateRender(false);
						VBOHandler vbo = c.end();
						chunk.buffer.v = v;
						chunk.buffer.add(vbo);
						//MyLogger.info("render update");
					}
					Minecraft.getMinecraft().entityRenderer.disableLightmap();
					GL11.glPopMatrix();
				}
			}
		}
	}

}
