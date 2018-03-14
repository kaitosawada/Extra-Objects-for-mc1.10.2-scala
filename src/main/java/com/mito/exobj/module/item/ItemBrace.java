package com.mito.exobj.module.item;

import java.util.List;

import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.item.*;
import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.lib.render.model.LineLoop;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.render.RenderHighLight;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.module.main.ResisterItem;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBrace extends ItemSet implements IMouseWheel, IHighLight, IMaterial, ISize {

	public byte key = 0;
	public static int colorMax = 16, sizeMax = 100;


	public ItemBrace() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		int isize = nbt.getInteger("size");
		return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(itemstack) + ".list") + " x" + isize).trim();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		super.addInformation(itemstack, player, list, b);
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		list.add("size : " + nbt.getInteger("size"));
		list.add("type : " + this.getType(itemstack));
		list.add("texture : " + this.getMaterial(itemstack).getLocalizedName());
		if (!this.getJoint(itemstack).equals(""))
			list.add("Joint : " + this.getJoint(itemstack));

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (String shape : BraceTypeRegistry.shapeList) {
			for (String joint : new String[]{"", "CubeJoint"}) {
				ItemStack itemstack = new ItemStack(item, 1, 0);
				NBTTagCompound nbt = new NBTTagCompound();
				itemstack.setTagCompound(nbt);
				this.setSize(itemstack, 5);
				this.setType(itemstack, shape);
				this.setMaterial(itemstack, Blocks.STONE);
				this.setJoint(itemstack, joint);
				nbt.setInteger("block", Block.getIdFromBlock(Blocks.WOOL));
				list.add(itemstack);
			}
		}
	}

	//should refactor
	@Override
	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, RayTraceResult movingOP, Vec3d set, Vec3d end, NBTTagCompound nbt) {
		boolean flag = true;
		ExtraObject eo = ChunkWorldManager.getWorldData(world).getBraceBaseByID(nbt.getInteger("ExtraObject"));
		ExtraObject eo2 = MyUtil.getBrace(movingOP);
		if (eo != null && eo2 != null && eo.BBID == eo2.BBID) {
			if (eo2 instanceof Brace) {
				Brace brace = (Brace) eo;
				if (brace.line.getStart().equals(end) || brace.line.getEnd().equals(end)) {
					if (brace.line instanceof LineLoop) {
						((LineLoop) brace.line).isLoop = true;
						brace.sync();
						brace.updateRenderer();
					} else if (!(brace.line instanceof Line)) {
						LineLoop ll = new LineLoop();
						ll.line.addAll(brace.line.getLine());
						ll.isLoop = true;
						brace.line = ll;
						brace.sync();
						brace.updateRenderer();
					}
					return;
				}
			}
		}
		if (eo != null && eo instanceof Brace) {
			Brace brace = (Brace) eo;
			if (brace.line.getStart().equals(set)) {
				LineLoop ll = new LineLoop();
				ll.line.add(end);
				ll.line.addAll(brace.line.getLine());
				brace.line = ll;
				brace.updateRenderer();
				brace.sync();
				flag = false;
			} else if (brace.line.getEnd().equals(set)) {
				LineLoop ll = new LineLoop();
				ll.line.addAll(brace.line.getLine());
				ll.line.add(end);
				brace.line = ll;
				brace.updateRenderer();
				brace.sync();
				flag = false;
			}
		}
		if (eo2 != null && eo2 instanceof Brace) {
			Brace brace = (Brace) eo2;
			if (brace.line.getStart().equals(end)) {
				LineLoop ll = new LineLoop();
				ll.line.add(set);
				ll.line.addAll(brace.line.getLine());
				brace.line = ll;
				brace.updateRenderer();
				brace.sync();
				flag = false;
			} else if (brace.line.getEnd().equals(end)) {
				LineLoop ll = new LineLoop();
				ll.line.addAll(brace.line.getLine());
				ll.line.add(set);
				brace.line = ll;
				brace.updateRenderer();
				brace.sync();
				flag = false;
			}
		}
		if (flag) {
			Brace brace = new Brace(world, set, end, this.getType(itemstack), getJoint(itemstack), this.getMaterial(itemstack), this.getColor(itemstack), this.getRealSize(itemstack));
			brace.addToWorld();
		}
	}

	@Override
	public void clientProcess(RayTraceResult mop, ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null && nbt.getBoolean("activated")) {
			Vec3d pos = mop.hitVec;
			Block texture = getMaterial(itemstack);
			//Main.proxy.playSound(new ResourceLocation(texture.getSoundType().getBreakSound(), texture.getSoundType().volume, texture.getSoundType().getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
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
			rh.drawFakeBrace(player, set, end, size, partialTicks);
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
		drawHighLightBrace(player, partialTicks, mop);
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

	public ItemStack setJoint(ItemStack itemstack, String name) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		nbt.setString("joint", name);
		return itemstack;
	}

	public String getType(ItemStack itemstack) {
		String ret = "square";
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("stype")) {
			ret = itemstack.getTagCompound().getString("stype");
		}
		return ret;
	}

	public String getJoint(ItemStack itemstack) {
		String ret = "";
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("joint")) {
			ret = itemstack.getTagCompound().getString("joint");
		}
		return ret;
	}

	public ItemStack setType(ItemStack itemstack, String i) {
		NBTTagCompound nbt = getNBTShareTag(itemstack);
		nbt.setString("stype", i);
		return itemstack;
	}
}
