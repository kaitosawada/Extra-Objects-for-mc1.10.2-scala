package com.mito.exobj.module.item;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.item.IHighLight;
import com.mito.exobj.lib.item.ItemSet;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.render.RenderHighLight;
import com.mito.exobj.lib.render.exorender.BezierCurve;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemBender extends ItemSet implements IHighLight {

	public byte key = 0;

	public ItemBender() {
		super();
		this.setMaxDamage(0);
		this.maxStackSize = 1;
	}

	@Override
	public boolean activate(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult mop, NBTTagCompound nbt, BB_Key key) {
		if (mop.entityHit != null && mop.entityHit instanceof EntityWrapper && ((EntityWrapper) mop.entityHit).base instanceof Brace) {
			Vec3d set = mop.hitVec;
			Brace brace = (Brace) ((EntityWrapper) mop.entityHit).base;
			if (key.isShiftPressed()) {
				if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					brace.line = new Line(b.points[0], b.points[3]);
					brace.sync();
					brace.updateRenderer();
				}
				return false;
			}
			nbt.setBoolean("isPos", set.distanceTo(brace.line.getStart()) < 0.01);
			nbt.setInteger("brace", brace.BBID);
			return true;
		}
		return false;
	}

	@Override
	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP, Vec3d set, Vec3d end, NBTTagCompound nbt) {
		ExtraObject base = ChunkWorldManager.getWorldData(world).getBraceBaseByID(nbt.getInteger("brace"));
		if (base != null && base instanceof Brace) {
			Brace brace = (Brace) base;
			if (nbt.getBoolean("isPos")) {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getStart(), end, MitoMath.ratio_vector(brace.line.getEnd(), end, 0.8), brace.line.getEnd());
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[1] = end;
				}
			} else {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getStart(), MitoMath.ratio_vector(brace.line.getStart(), end, 0.8), end, brace.line.getEnd());
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[2] = end;
				}
			}
			brace.sync();
			brace.updateRenderer();
		}
	}

	@Override
	public boolean drawHighLight(ItemStack itemstack, EntityPlayer player, float partialTicks, RayTraceResult mop) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		if (mop == null || !MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop))
			return false;

		RenderHighLight rh = RenderHighLight.INSTANCE;
		if (nbt.getBoolean("activated")) {
			Vec3d end = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			rh.drawFakeBraceBend(player, mop.hitVec, nbt, partialTicks);
		} else {
			if (mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapper && ((EntityWrapper) mop.entityHit).base instanceof Brace) {
				rh.drawCenter(player, mop.hitVec, ((Brace) ((EntityWrapper) mop.entityHit).base).size / 2 + 0.1, partialTicks);
				this.drawHighLightBrace(player, partialTicks, mop);
			}
		}
		return true;
	}
}
