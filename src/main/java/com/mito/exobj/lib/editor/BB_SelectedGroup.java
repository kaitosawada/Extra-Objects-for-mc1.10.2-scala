package com.mito.exobj.lib.editor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.lib.ModelObject;
import com.mito.exobj.lib.data.BB_OutputHandler;
import com.mito.exobj.lib.render.*;
import com.mito.exobj.lib.render.model.BB_ModelGroup;
import com.mito.exobj.module.main.ClientProxy;
import com.mito.exobj.module.main.FileLoadManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.lib.render.RenderHandler;
import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.network.EditerPacketProcessor;
import com.mito.exobj.lib.network.EditerPacketProcessor.EnumGroupMode;
import com.mito.exobj.lib.network.PacketHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BB_SelectedGroup {

	public enum SelectMode {
		Copy, Move, Block, None,
	}

	public List<ExtraObject> list = new ArrayList<>();
	public ClientProxy proxy;
	public Vec3d set = new Vec3d(0, 0, 0);
	public boolean activated = false;
	public int pasteNum = 0;
	public int size = 100;
	public int roll = 0;
	public int pitch = 0;
	public int yaw = 0;
	public SelectMode mode = SelectMode.None;

	public BB_SelectedGroup(ClientProxy px) {
		this.proxy = px;
	}

	public void initNum() {
		size = 100;
		roll = 0;
		pitch = 0;
		yaw = 0;
	}

	public void addShift(ExtraObject... bases) {
		initNum();
		for (int n = 0; n < bases.length; n++) {
			if (this.list.contains(bases[n])) {
				this.list.remove(bases[n]);
			} else {
				this.list.add(bases[n]);
			}
		}
	}

	public void addShift(List<ExtraObject> bases) {
		initNum();
		for (int n = 0; n < bases.size(); n++) {
			if (this.list.contains(bases.get(n))) {
				this.list.remove(bases.get(n));
			} else {
				this.list.add(bases.get(n));
			}
		}
	}

	public void replace(List<ExtraObject> bases) {
		initNum();
		this.list = bases;
	}

	public void replace(ExtraObject base) {
		initNum();
		this.list.clear();
		this.list.add(base);
	}

	public void remove(ExtraObject... bases) {
		initNum();
		for (int n = 0; n < bases.length; n++) {
			this.list.remove(bases[n]);
		}
	}

	public void delete() {
		initNum();
		this.list.clear();
	}

	public List<ExtraObject> getList() {
		return this.list;
	}

	public boolean drawHighLightGroup(EntityPlayer player, float partialticks) {
		if (this.list.isEmpty()) {
			return false;
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
		float size = 2.0F;
		BraceHighLightHandler data = proxy.bh;
		if (data.key == null || !data.key.equals(this)) {
			data.buffer.delete();
			CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
			c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
			c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			for (ExtraObject base : list) {
				BB_Render render = ExtraObjectRegistry.getBraceBaseRender(base);
				render.updateRender(c, base, base.getPos());
			}
			data.key = this;
			VBOHandler vbo = c.end();
			data.buffer.add(vbo);
		}

		GL11.glPushMatrix();

		GL11.glLineWidth(size);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		RenderHandler.enableClient();
		data.buffer.draw(GL11.GL_LINE_LOOP);
		RenderHandler.disableClient();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
		return true;
	}

	public boolean drawHighLightCopy(EntityPlayer player, float partialticks, RayTraceResult mop) {
		if (this.list.isEmpty()) {
			return false;
		}
		for (int n = 0; n < this.list.size(); n++) {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
					-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
					-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
			Vec3d pos = this.getDistance(mop);
			GL11.glTranslated(pos.xCoord, pos.yCoord, pos.zCoord);
			BB_Render render = ExtraObjectRegistry.getBraceBaseRender(this.list.get(n));
			render.drawHighLight(this.list.get(n), partialticks);//4.0F
			GL11.glPopMatrix();
		}
		return true;
	}

	public Vec3d getDistance(RayTraceResult mop) {
		if (list.isEmpty()) {
			return null;
		}
		Vec3d c = getCenter();
		return mop.hitVec.addVector(-c.xCoord, -c.yCoord, -c.zCoord);
	}

	public void applyProperty(Block tex, int color, String shape) {
		for (ExtraObject e : list) {
			if (e instanceof Brace) {
				Brace brace = ((Brace) e);
				if (tex != null) {
					brace.texture = tex;
				}
				if (shape != null) {
					brace.shape = shape;
					//brace.shouldUpdateRender = true;
				}
				if (color >= 0 && color < 16) {
					brace.color = color;
				} else if (color == 16) {
				} else {
					brace.color = 0;
				}
				e.sync();
			}
		}
	}

	public void applyColor(int color) {
		for (ExtraObject e : list) {
			if (e instanceof Brace) {
				Brace brace = ((Brace) e);

				if (color >= 0 && color < 16) {
					brace.color = color;
				} else if (color == 16) {
				} else {
					brace.color = 0;
				}
				e.sync();
			}
		}
	}

	public void init() {
		this.delete();
		this.activated = false;
		this.mode = SelectMode.None;
	}

	public void applySize(int isize) {
		for (ExtraObject e : list) {
			if (e instanceof Brace) {
				Brace brace = ((Brace) e);
				brace.size = (double) isize * 0.05;
				e.updateRenderer();
				e.sync();
			}
		}
	}

	public int getSize() {
		if (this.list.isEmpty()) {
			return -1;
		} else if (this.list.size() == 1 && list.get(0) instanceof Brace) {
			return (int) (((Brace) list.get(0)).size * 20);
		} else {
			int is = (int) (((Brace) list.get(0)).size * 20);
			for (ExtraObject e : list) {
				if (e instanceof Brace) {
					Brace brace = ((Brace) e);
					if (is != (int) (brace.size * 20)) {
						return -1;
					}
				}
			}
			return is;
		}
	}

	public void applyRoll(int iroll) {
		for (ExtraObject e : list) {
			if (e instanceof Brace) {
				Brace brace = ((Brace) e);
				brace.setRoll(iroll);
			}
		}
	}

	public void applyGroupSize(int isize) {
		Vec3d c = getCenter();
		for (ExtraObject e : list) {
			ExtraObject brace = e;
			brace.scale(c, (double) isize / this.size);

		}
		this.size = isize;
	}

	public void applyGroupRot(int iroll, int ipitch, int iyaw) {
		Vec3d c = getCenter();
		for (ExtraObject e : list) {
			e.rotate(c, -this.roll + iroll, -this.pitch + ipitch, -this.yaw + iyaw);
		}
		this.roll = iroll;
		this.pitch = ipitch;
		this.yaw = iyaw;
	}

	public Vec3d getCenter() {
		//return Vec3d.createVectorHelper(bases.get(0).pos.xCoord, this.getMinY(), bases.get(0).pos.zCoord);
		return set;
	}

	public void breakGroup() {
		/*for (int n = 0; n < getList().size(); n++) {
			ExtraObject base = getList().get(n);
			base.breakBrace(Main.proxy.getClientPlayer());
		}*/
		PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.DELETE, getList()));
	}

	public void grouping() {
		PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.GROUPING, getList()));
		this.delete();
	}

	public void outputObj(){
		File dir = FileLoadManager.objDir;
		LocalDateTime date = LocalDateTime.now();
		String name = "model_" + date.format(DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss"));
		BB_ModelGroup model = new BB_ModelGroup();
		for (ExtraObject e : list) {
			if(e instanceof ModelObject){
				model.add(((ModelObject)e).getModel());
			}
		}
		try {
			BB_OutputHandler.outputObj(name, dir, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
