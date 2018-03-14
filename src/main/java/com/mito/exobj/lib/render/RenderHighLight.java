package com.mito.exobj.lib.render;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class RenderHighLight {

	public static RenderHighLight INSTANCE = new RenderHighLight();

	public void drawRuler(EntityPlayer player, Vec3d v1, Vec3d v2, int div, double partialTicks) {
		Vec3d partV12 = MitoMath.vectorDiv(v2.addVector(-v1.xCoord, -v1.yCoord, -v1.zCoord), (double) div);

		GL11.glPushMatrix();
		GL11.glTranslated(v1.xCoord - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				v1.yCoord - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				v1.zCoord - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		for (int n = 0; n < (div + 1); n++) {
			this.renderLine(0.0, 0.0, 0.1);
			this.renderLine(0.0, 0.0, -0.1);
			this.renderLine(0.0, 0.1, 0.0);
			this.renderLine(0.0, -0.1, 0.0);
			this.renderLine(0.1, 0.0, 0.0);
			this.renderLine(-0.1, 0.0, 0.0);
			GL11.glTranslated(partV12.xCoord, partV12.yCoord, partV12.zCoord);
		}

		GL11.glPopMatrix();

	}

	public void drawCenter(EntityPlayer player, Vec3d set, double partialTicks) {
		drawCenter(player, set, 0.1, partialTicks);
	}

	public void drawCenter(EntityPlayer player, Vec3d set, double size, double partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(set.xCoord - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				set.yCoord - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				set.zCoord - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		this.renderLine(0.0, 0.0, size);
		this.renderLine(0.0, 0.0, -size);
		this.renderLine(0.0, size, 0.0);
		this.renderLine(0.0, -size, 0.0);
		this.renderLine(size, 0.0, 0.0);
		this.renderLine(-size, 0.0, 0.0);
		GL11.glPopMatrix();
	}

	public void drawLine(EntityPlayer player, Vec3d set, Vec3d end, double partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(set.xCoord - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				set.yCoord - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				set.zCoord - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		this.renderLine(MitoMath.sub_vector(end, set));
		GL11.glPopMatrix();
	}

	public void drawBox(EntityPlayer player, Vec3d set, double size, double partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(set.xCoord - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				set.yCoord - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				set.zCoord - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		this.renderBox(size);
		GL11.glPopMatrix();
	}

	public void drawBox(EntityPlayer player, Vec3d set, Vec3d end, double partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated((set.xCoord + end.xCoord) / 2 - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				(set.yCoord + end.yCoord) / 2 - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				(set.zCoord + end.zCoord) / 2 - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));

		this.renderBox(Math.abs((set.xCoord - end.xCoord)) / 2 + 0.01, Math.abs((set.yCoord - end.yCoord)) / 2 + 0.01, Math.abs((set.zCoord - end.zCoord)) / 2 + 0.01, false);

		GL11.glPopMatrix();
	}

	public void drawFakeBrace(EntityPlayer player, Vec3d set, Vec3d end, double size, double partialTicks) {

		GL11.glPushMatrix();
		GL11.glTranslated((end.xCoord + set.xCoord) / 2 - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				(end.yCoord + set.yCoord) / 2 - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				(end.zCoord + set.zCoord) / 2 - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		GL11.glPushMatrix();

		this.renderBox(size, (set.xCoord - end.xCoord) / 2, (set.yCoord - end.yCoord) / 2, (set.zCoord - end.zCoord) / 2);

		GL11.glPopMatrix();

		GL11.glPushMatrix();

		this.renderBox(size, (end.xCoord - set.xCoord) / 2, (end.yCoord - set.yCoord) / 2, (end.zCoord - set.zCoord) / 2);

		GL11.glPopMatrix();

		GL11.glPushMatrix();

		this.renderBrace(set, end, size);

		GL11.glPopMatrix();

		GL11.glPopMatrix();

	}

	public void drawFakeWall(EntityPlayer player, Vec3d set, Vec3d end, double width, double height, double partialTicks) {

		GL11.glPushMatrix();
		GL11.glTranslated((end.xCoord + set.xCoord) / 2 - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				(end.yCoord + set.yCoord) / 2 - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				(end.zCoord + set.zCoord) / 2 - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));

		double l = MitoMath.subAbs(set, end);

		float a1, a2;

		double xabs = set.xCoord - end.xCoord;
		double xzabs = Math.sqrt(Math.pow(set.xCoord - end.xCoord, 2) + Math.pow(set.zCoord - end.zCoord, 2));
		xzabs = set.xCoord - end.xCoord >= 0 ? -xzabs : xzabs;

		a1 = (float) (Math.atan((set.yCoord - end.yCoord) / xzabs) / Math.PI * 180);
		a2 = (float) (Math.atan((set.zCoord - end.zCoord) / xabs) / Math.PI * 180);

		GL11.glRotatef(-a2, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(a1, 0.0F, 0.0F, -1.0F);

		renderBox(l / 2, height / 2, width / 2);

		GL11.glPopMatrix();

	}

	private void renderBox(double size, double cx, double cy, double cz) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		double x = cx + size / 2;
		double y = cy + size / 2;
		double z = cz + size / 2;
		double x1 = cx - size / 2;
		double y1 = cy - size / 2;
		double z1 = cz - size / 2;
		GL11.glLineWidth(2.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y1, z);
		GL11.glVertex3d(x, y, z1);
		GL11.glVertex3d(x, y1, z1);
		GL11.glVertex3d(x1, y, z);
		GL11.glVertex3d(x1, y1, z);
		GL11.glVertex3d(x1, y, z1);
		GL11.glVertex3d(x1, y1, z1);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y, z1);
		GL11.glVertex3d(x, y1, z);
		GL11.glVertex3d(x, y1, z1);
		GL11.glVertex3d(x1, y, z);
		GL11.glVertex3d(x1, y, z1);
		GL11.glVertex3d(x1, y1, z);
		GL11.glVertex3d(x1, y1, z1);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x1, y, z);
		GL11.glVertex3d(x, y, z1);
		GL11.glVertex3d(x1, y, z1);
		GL11.glVertex3d(x, y1, z);
		GL11.glVertex3d(x1, y1, z);
		GL11.glVertex3d(x, y1, z1);
		GL11.glVertex3d(x1, y1, z1);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void renderBox(double s, boolean b) {
		double size = s / 2.0;
		this.renderBox(size, size, size, b);
	}

	public void renderBox(double s) {
		double size = s / 2.0;
		this.renderBox(size, size, size);
	}

	private void renderBox(double x, double y, double z) {
		renderBox(x, y, z, true);
	}

	private void renderBox(double x, double y, double z, boolean b) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (b) {
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		}
		GL11.glLineWidth(2.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, -y, z);
		GL11.glVertex3d(x, y, -z);
		GL11.glVertex3d(x, -y, -z);
		GL11.glVertex3d(-x, y, z);
		GL11.glVertex3d(-x, -y, z);
		GL11.glVertex3d(-x, y, -z);
		GL11.glVertex3d(-x, -y, -z);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y, -z);
		GL11.glVertex3d(x, -y, z);
		GL11.glVertex3d(x, -y, -z);
		GL11.glVertex3d(-x, y, z);
		GL11.glVertex3d(-x, y, -z);
		GL11.glVertex3d(-x, -y, z);
		GL11.glVertex3d(-x, -y, -z);

		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(-x, y, z);
		GL11.glVertex3d(x, y, -z);
		GL11.glVertex3d(-x, y, -z);
		GL11.glVertex3d(x, -y, z);
		GL11.glVertex3d(-x, -y, z);
		GL11.glVertex3d(x, -y, -z);
		GL11.glVertex3d(-x, -y, -z);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}

	public void renderLine(double x, double y, double z) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		GL11.glLineWidth(2.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(0, 0, 0);
		GL11.glVertex3d(x, y, z);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}

	public void renderLine(Vec3d c) {

		renderLine(c.xCoord, c.yCoord, c.zCoord);

	}

	public void renderLine(Vec3d c, Vec3d p, double size) {

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		GL11.glLineWidth((float) size);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(c.xCoord, c.yCoord, c.zCoord);
		GL11.glVertex3d(p.xCoord, p.yCoord, p.zCoord);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}

	public void renderBrace(Vec3d set, Vec3d end, double size) {

		double l = MitoMath.subAbs(set, end);

		double x1, y1, z1;
		float a1, a2;

		double xabs = set.xCoord - end.xCoord;
		double xzabs = Math.sqrt(Math.pow(set.xCoord - end.xCoord, 2) + Math.pow(set.zCoord - end.zCoord, 2));
		xzabs = set.xCoord - end.xCoord >= 0 ? -xzabs : xzabs;

		a1 = (float) (Math.atan((set.yCoord - end.yCoord) / xzabs) / Math.PI * 180);
		a2 = (float) (Math.atan((set.zCoord - end.zCoord) / xabs) / Math.PI * 180);

		GL11.glRotatef(-a2, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(a1, 0.0F, 0.0F, -1.0F);

		renderBox(l / 2, size / 2, size / 2);
	}

	private static void drawGrid(EntityPlayer player, double partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		//GL11.glTranslated((float) aEvent.target.blockX + 0.5F, (float) aEvent.target.blockY + 0.5F, (float) aEvent.target.blockZ + 0.5F);
		//int side = aEvent.target.sideHit;//Rotation.sideRotations[aEvent.target.sideHit].glApply();
		//MitoUtil.rotation(side);
		GL11.glTranslated(0.0D, -0.501D, 0.0D);
		GL11.glLineWidth(2.0F);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
		//GL11.glEnable(GL11.GL_BLEND);
		GL11.glBegin(1);
		GL11.glVertex3d(0.5D, 0.0D, -0.25D);
		GL11.glVertex3d(-0.5D, 0.0D, -0.25D);
		GL11.glVertex3d(0.5D, 0.0D, 0.25D);
		GL11.glVertex3d(-0.5D, 0.0D, 0.25D);
		GL11.glVertex3d(0.25D, 0.0D, -0.5D);
		GL11.glVertex3d(0.25D, 0.0D, 0.5D);
		GL11.glVertex3d(-0.25D, 0.0D, -0.5D);
		GL11.glVertex3d(-0.25D, 0.0D, 0.5D);
		GL11.glEnd();
		GL11.glPopMatrix();

		//GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawFakeBraceBend(EntityPlayer player, Vec3d end, NBTTagCompound nbt, double partialTicks) {
		ExtraObject base = ChunkWorldManager.getWorldData(player.worldObj).getBraceBaseByID(nbt.getInteger("brace"));

		if (base != null && base instanceof Brace) {
			Brace brace = (Brace) base;
			Vec3d v1 = brace.line.getStart();
			Vec3d v2 = end;
			Vec3d v4 = brace.line.getEnd();
			Vec3d v3 = end;
			drawBezier(player, v1, v2, v3, v4, 4, partialTicks);
		}
	}

	public void drawBezier(EntityPlayer player, Vec3d d1, Vec3d d2, Vec3d d3, Vec3d d4, double size, double partialTicks) {

		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks));
		GL11.glPushMatrix();
		Vec3d v1;
		Vec3d v2;
		for (int i = 0; i < 20; i++) {
			v1 = MitoMath.vectorBezier(d1, d2, d3, d4, (double) i * 0.05);
			v2 = MitoMath.vectorBezier(d1, d2, d3, d4, (double) (i + 1) * 0.05);
			renderLine(v1, v2, size);
		}

		GL11.glPopMatrix();

		GL11.glPopMatrix();

	}

}
