package com.mito.exobj.module.exobject;

import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import com.mito.exobj.lib.render.exorender.BezierCurve;
import com.mito.exobj.lib.render.exorender.IJoint;
import com.mito.exobj.lib.render.model.*;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.module.item.ItemBrace;
import com.mito.exobj.module.main.ResisterItem;
import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.lib.ModelObject;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class Brace extends ModelObject {

	// pos > cpoint > end
	//public Vec3d rand = new Vec3d(Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001);
	public ILineBrace line = null;
	public String shape;
	public String joint;
	public double size;

	public Brace(World world) {
		super(world);
	}

	public Brace(World world, Vec3d pos) {
		super(world, pos);
	}

	public Brace(World world, Vec3d pos, Vec3d end, String shape, String joint, Block material, int tex, double size) {
		this(world, pos);
		this.line = new Line(pos, end);
		this.shape = shape;
		this.size = size;
		this.texture = material;
		this.color = tex;
		this.joint = joint;
	}

	@Override
	public void onUpdate() {
	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		//this.line.readNBT(nbt);
		if (nbt.hasKey("line")) {
			switch (nbt.getInteger("line")) {
				case 0:
					Vec3d start = getVec3(nbt, "start");
					Vec3d end = getVec3(nbt, "end");
					line = new Line(start, end);
					break;
				case 1:

					Vec3d v1 = getVec3(nbt, "bezier1");
					Vec3d v2 = getVec3(nbt, "bezier2");
					Vec3d v3 = getVec3(nbt, "bezier3");
					Vec3d v4 = getVec3(nbt, "bezier4");
					line = new BezierCurve(v1, v2, v3, v4);
					break;
				case 2:
					NBTTagList nbtList = nbt.getTagList("line_list", 10);
					if (nbtList.tagCount() > 1) {
						Vec3d[] vs = new Vec3d[nbtList.tagCount()];
						for (int l = 0; l < nbtList.tagCount(); ++l) {
							NBTTagCompound nbt1 = nbtList.getCompoundTagAt(l);
							vs[l] = getVec3(nbt1, "vec");
						}
						line = new LineLoop(vs);
						((LineLoop)line).isLoop = nbt.getBoolean("isLoop");

					} else {
						this.setDead();
						line = new Line(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0));
					}
					break;
				default:
					this.setDead();
					line = new Line(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0));
					break;
			}
		} else {
			Vec3d end = getVec3(nbt, "end");
			line = new Line(pos, end);
		}
		this.shape = nbt.getString("shape");
		this.joint = nbt.getString("joint");
		this.size = nbt.getDouble("size");
		this.roll = nbt.getDouble("roll");
		super.readExtraObjectFromNBT(nbt);
	}

	void setVec3(NBTTagCompound nbt, String name, Vec3d vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	Vec3d getVec3(NBTTagCompound nbt, String name) {
		return new Vec3d(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		//MyLogger.info("save brace id " + this.BBID);
		if (line != null && shape != null) {
			line.writeNBT(nbt);
			nbt.setString("shape", this.shape);
			nbt.setString("joint", this.joint);
			nbt.setDouble("size", this.size);
			nbt.setDouble("roll", this.roll);
			super.writeExtraObjectToNBT(nbt);
		}
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB aabb) {
		if (line == null) {
			return false;
		}
		boolean ret = false;
		List<Line> la = line.getSegments();
		for (Line line : la) {
			if (line.interactWithAABB(aabb, size)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public Line interactWithRay(Vec3d set, Vec3d end) {
		if (line == null) {
			return null;
		}
		if (line.getStart().distanceTo(line.getEnd()) < 0.01) {
			Vec3d ve = MitoMath.getNearPoint(set, end, line.getStart());
			if (ve.distanceTo(line.getStart()) < size / 1.5) {
				return new Line(ve, line.getStart());
			}
		}
		Line ret = null;
		List<Line> list = line.getSegments();
		for (Line l : list) {
			Line line2 = MitoMath.getDistanceLine(set, end, l.start, l.end);
			if (line2.getLength() < size / 1.5 && !(MyUtil.isVecEqual(line2.end, line.getStart()) || MyUtil.isVecEqual(line2.end, line.getEnd()))) {
				if (ret == null || line2.end.distanceTo(set) < ret.end.distanceTo(set)) {
					ret = line2;
				}
			}
		}
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void particle() {
		if (line == null) {
			return;
		}
		List<Line> list = line.getSegments();
		for (Line l : list) {
			int b0 = (int) (this.size * 3) + 1;
			Vec3d center = MitoMath.ratio_vector(l.start, l.end, 0.5);
			int div = (int) (l.getLength() * 3) + 1;
			//MyLogger.info(this.line.getLength());

			for (int i1 = 0; i1 < b0; ++i1) {
				for (int j1 = 0; j1 < b0; ++j1) {
					for (int k1 = 0; k1 < div; ++k1) {
						Vec3d vec = MitoMath.ratio_vector(l.start, l.end, (double) k1 / (double) div);
						double d0 = vec.xCoord + ((double) j1 * size) / (double) b0 - (size / 2);
						double d1 = vec.yCoord + ((double) i1 * size) / (double) b0 - (size / 2);
						double d2 = vec.zCoord + ((double) j1 * size) / (double) b0 - (size / 2);
						Main.proxy.addDiggingEffect(worldObj, center, d0, d1, d2, this.texture, color);
					}
				}
			}
		}
	}

	public void dropItem() {

		float f = this.random.nextFloat() * 0.2F + 0.1F;
		float f1 = this.random.nextFloat() * 0.2F + 0.1F;
		float f2 = this.random.nextFloat() * 0.2F + 0.1F;

		ItemBrace brace = (ItemBrace) ResisterItem.ItemBrace;
		ItemStack itemstack1 = new ItemStack(ResisterItem.ItemBrace, 1, this.color);
		brace.setSize(itemstack1, (int) (this.size * 20));
		brace.setType(itemstack1, this.shape);
		brace.setDamage(itemstack1, color);
		brace.setMaterial(itemstack1, texture);

		NBTTagCompound nbt = itemstack1.getTagCompound();
		itemstack1.setTagCompound(nbt);
		nbt.setBoolean("activated", false);
		nbt.setDouble("setX", 0.0D);
		nbt.setDouble("setY", 0.0D);
		nbt.setDouble("setZ", 0.0D);
		nbt.setBoolean("useFlag", false);
		EntityItem entityitem = new EntityItem(worldObj, (double) ((float) this.pos.xCoord + f), (double) ((float) this.pos.yCoord + f1), (double) ((float) this.pos.zCoord + f2), itemstack1);

		float f3 = 0.05F;
		entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
		entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
		entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
		worldObj.spawnEntityInWorld(entityitem);
	}

	/*@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float i) {
		return getBrightnessForRender(i, this.getPos()pos.xCoord, pos.yCoord, pos.zCoord);
	}*/

	@Override
	public AxisAlignedBB getBoundingBox() {
		if (line == null) {
			return null;
		}
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		List<Vec3d> list = line.getLine();
		for (Vec3d v : list) {
			maxX = maxX > v.xCoord ? maxX : v.xCoord;
			maxY = maxY > v.yCoord ? maxY : v.yCoord;
			maxZ = maxZ > v.zCoord ? maxZ : v.zCoord;
			minX = minX < v.xCoord ? minX : v.xCoord;
			minY = minY < v.yCoord ? minY : v.yCoord;
			minZ = minZ < v.zCoord ? minZ : v.zCoord;
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(size, size, size);
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		if (line == null) {
			return;
		}
		double le = line.getLength();
		double d = le < size ? le : size;
		List<Line> list = line.getSegments();
		for (Line l : list) {
			l.addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity, d);
		}
	}

	public RayTraceResult snap(RayTraceResult mop) {
		return this.line.snap(mop);
	}

	@Override
	public IDrawable getModel() {
		updateModel();
		return model;
	}

	@Override
	public void updateModel() {
		BB_ModelGroup ret = new BB_ModelGroup();
		IJoint ij = BraceTypeRegistry.getJoint(this.joint);
		IDrawBrace id = BraceTypeRegistry.getFigure(shape);
		if (ij != null)
			ret.add(ij.getModel(this, pos));
		if (id != null)
			ret.add(id.getModel(line, size, roll, pos));
		model = ret;
	}

	private double roll = 0;

	public void setRoll(double r) {
		this.roll = r;
		sync();
		updateRenderer();
	}

	public double getRoll() {
		return this.roll;
	}

	@Override
	public void transform(Mat4 m) {
		this.line.transform(m);
		super.transform(m);
	}

	@Override
	public void scale(Vec3d cent, double i) {
		this.size = size * i;
		super.scale(cent, i);
	}

	/*public boolean rightClick(EntityPlayer player, Vec3d pos, ItemStack itemstack) {
		if (player.capabilities.isCreativeMode) {
		}
		return false;
	}*/

}
