package com.mito.exobj.module.item;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.item.*;
import com.mito.exobj.lib.render.RenderHighLight;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.module.exobject.Tofu;
import com.mito.exobj.module.exobject.Wall;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.module.main.ResisterItem;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.mito.exobj.module.item.ItemBrace.sizeMax;

public class ItemWall extends ItemSet implements IHighLight, IMaterial, ISize, IMouseWheel {


	public ItemWall() {
		super();
	}

	@Override
	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP,
							  Vec3d set, Vec3d end, NBTTagCompound nbt) {
		int color = this.getColor(itemstack);
		Wall wall = new Wall(world, set, end, this.getMaterial(itemstack), color, getRealSize(itemstack));
		wall.addToWorld();

		if (!player.capabilities.isCreativeMode) {
			itemstack.stackSize--;
			if (itemstack.stackSize == 0) {
				//player.destroyCurrentEquippedItem();
			}
		}
	}

	@Override
	public boolean drawHighLight(ItemStack itemstack, EntityPlayer player, float partialTicks, RayTraceResult mop) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		double size = this.getRealSize(itemstack);
		if (mop == null || !MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop))
			return false;
		Vec3d set = mop.hitVec;

		RenderHighLight rh = RenderHighLight.INSTANCE;
		if (nbt.getBoolean("activated")) {
			Vec3d end = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			Vec3d v = new Vec3d(set.xCoord, (set.yCoord + end.yCoord) / 2, set.zCoord);
			Vec3d v1 = new Vec3d(end.xCoord, (set.yCoord + end.yCoord) / 2, end.zCoord);
			rh.drawFakeWall(player, v, v1, size, Math.abs(set.yCoord - end.yCoord), partialTicks);
		} else {
			ExtraObject base = null;
			if (mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapper) {
				base = ((EntityWrapper) mop.entityHit).base;
			}
			if (base != null && base instanceof Brace && size < ((Brace) base).size) {
				rh.drawCenter(player, set, ((Brace) base).size / 2 + 0.1, partialTicks);
			} else {
				rh.drawBox(player, set, size, partialTicks);
			}
		}
		return true;
	}

	@Override
	public boolean wheelEvent(EntityPlayer player, ItemStack stack, RayTraceResult mop, BB_Key key, int dwheel) {
		if (key.isShiftPressed()) {
			ItemBrace brace = (ItemBrace) ResisterItem.ItemBrace;
			int w = dwheel / 120;
			int size = brace.getSize(stack) + w;
			if (size > sizeMax) {
				size = sizeMax;
			} else if (size < 1) {
				size = 1;
			}
			brace.setSize(stack, size);
			return true;
		}
		return false;
	}
}
