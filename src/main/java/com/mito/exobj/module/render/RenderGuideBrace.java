package com.mito.exobj.module.render;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.render.BB_Render;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.exobject.Brace;

import net.minecraft.util.math.Vec3d;

public class RenderGuideBrace extends BB_Render {

	public void doRender(ExtraObject base, float partialTickTime) {
		Brace brace = (Brace) base;
		Vec3d a = brace.line.getStart();
		Vec3d b = brace.line.getEnd();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(5.0F);
		GL11.glColor4f(0.8F, 0.8F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(a.xCoord, a.yCoord, a.zCoord);
		GL11.glVertex3d(b.xCoord, b.yCoord, b.zCoord);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
