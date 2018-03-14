package com.mito.exobj.lib;

import com.mito.exobj.lib.render.model.IDrawable;
import com.mito.exobj.lib.render.model.Mat4;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.item.ItemBar;
import com.mito.exobj.lib.item.ItemBraceBase;
import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.utilities.MyLogger;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelObject extends ExtraObject {

	public int color = 0;
	public Block texture = Blocks.STONE;
	protected IDrawable model = null;

	public ModelObject(World world) {
		super(world);
	}

	public ModelObject(World world, Vec3d pos) {
		super(world, pos);
	}


	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		this.texture = Block.getBlockById(nbt.getInteger("block"));
		this.color = nbt.getInteger("color");
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		nbt.setInteger("block", Block.getIdFromBlock(texture));
		nbt.setInteger("color", this.color);
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB boundingBox) {
		return false;
	}

	@Override
	public Line interactWithRay(Vec3d set, Vec3d end) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void particle() {

	}

	@Override
	public void updateRenderer() {
		if (datachunk != null)
			this.datachunk.updateRenderer();
		updateModel();

	}

	@Override
	public boolean leftClick(EntityPlayer player, ItemStack itemstack) {
			if (player.capabilities.isCreativeMode) {
				if (itemstack == null || itemstack.getItem() instanceof ItemBraceBase) {
					this.breakBrace(player);
				}
				return true;
			} else if (itemstack != null && itemstack.getItem() instanceof ItemBar) {
				this.breakBrace(player);
			}
		return false;
	}

	@Override
	public boolean rightClick(EntityPlayer player, Vec3d pos, ItemStack itemstack) {
		if (itemstack != null && itemstack.getItem() instanceof ItemBar) {
			this.breakBrace(player);
			return true;
		} else if (Main.proxy.isShiftKeyDown() && itemstack != null && Block.getBlockFromItem(itemstack.getItem()) != null) {
			//MyLogger.info(Block.getBlockFromItem(itemstack.getItem()).toString());
			this.texture = Block.getBlockFromItem(itemstack.getItem());
			this.color = itemstack.getItemDamage() % 16;
			this.updateRenderer();
			sync();
			return true;
		} else if (Main.proxy.isShiftKeyDown()) {
			this.lock(player);
			return true;
		}
		return false;
	}

	@Override
	public void dropItem() {
	}

	/*public AxisAlignedBB getBoundingBox() {
		return null;
	}*/

	@Override
	public Vec3d getPos() {
		return this.pos;
	}

	public IDrawable getModel() {
		if (model == null) {
			updateModel();
		}
		return model;
	}

	public void updateModel() {
	}

	@Override
	public void transform(Mat4 m) {
		this.pos = m.transformVec3d(this.pos);
		sync();
		updateRenderer();
	}
}
