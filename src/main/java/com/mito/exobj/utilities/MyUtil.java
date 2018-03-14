package com.mito.exobj.utilities;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.lib.EntityWrapper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.render.model.BB_Polygon;
import com.mito.exobj.lib.render.model.Mat4;
import com.mito.exobj.lib.render.model.Triangle;
import com.mito.exobj.lib.render.model.Vertex;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MyUtil {
	public static Mat4 getRotationMatrix(Vec3d dir) {
		return getRotationMatrix(dir, new Vec3d(0, -1, 0));
	}

	public static Mat4 getRotationMatrix(Vec3d dir, Vec3d v) {
		Vec3d v1, v2;
		Vec3d norm = dir.normalize();

		if (norm.crossProduct(v).lengthVector() > 0.01) {
			v1 = norm.crossProduct(v).normalize();
			v2 = norm.crossProduct(v1).normalize();
		} else {
			v1 = norm.crossProduct(new Vec3d(1, 0, 0)).normalize();
			v2 = norm.crossProduct(v1).normalize();
		}

		Mat4 ret = Mat4.createMat4(v1, v2, norm);
		return ret;
	}

	public static Vertex generateVertex(Triangle t, double u, double v, double texu, double texv) {
		double ua = t.vertexs[1].u - t.vertexs[0].u;
		double ub = t.vertexs[2].u - t.vertexs[0].u;
		double uc = u - t.vertexs[0].u;
		double va = t.vertexs[1].v - t.vertexs[0].v;
		double vb = t.vertexs[2].v - t.vertexs[0].v;
		double vc = v - t.vertexs[0].v;

		double a = (ub * vc - vb * uc) / (ub * va - vb * ua);
		double b = (ua * vc - va * uc) / (ua * vb - va * ub);

		return new Vertex(MitoMath.vectorSum(MitoMath.vectorMul(t.vertexs[0].pos.subtract(t.vertexs[1].pos), a), MitoMath.vectorMul(t.vertexs[0].pos.subtract(t.vertexs[2].pos), b)), texu, texv);

	}

	public static boolean checkInside(Triangle t, double u, double v) {
		boolean[] c = new boolean[3];
		for (int n = 0; n < 3; n++) {
			int n1 = (n + 1) % 3;
			c[n] = 0 > (t.vertexs[n].u - t.vertexs[n1].u) * (t.vertexs[n].v - v) - (t.vertexs[n].v - t.vertexs[n1].v) * (t.vertexs[n].u - u);
		}

		return (c[0] ^ c[1]) & (c[1] ^ c[2]);
	}

	public static Triangle[] decomposeLine(Triangle tri, int d) {
		return null;
	}

	public static List<Triangle> decomposePolygon(List<Vertex> list) {
		if (list.size() < 3) {
			return null;
		}
		List<Triangle> ret = new ArrayList<Triangle>();
		Vec3d curcross = null;
		int curVer = getfar(list);

		while (!list.isEmpty()) {
			if (list.size() == 3) {
				ret.add(new Triangle(list.get(0), list.get(1), list.get(2)));
				break;
			}
			int ne = curVer == list.size() - 1 ? 0 : curVer + 1;
			int pr = curVer == 0 ? list.size() - 1 : curVer - 1;
			Vertex curVec = list.get(curVer);
			Vertex prVec = list.get(pr);
			Vertex neVec = list.get(ne);
			if (curcross == null) {
				curcross = MitoMath.getNormal(curVec, neVec, prVec);
			} else {
				Vec3d cross = MitoMath.getNormal(curVec, neVec, prVec);
				if (cross.dotProduct(curcross) < 0) {
					curVer = curVer == list.size() - 1 ? 0 : curVer + 1;
					continue;
				}
			}
			if (!includePoint(curVec.pos, neVec.pos, prVec.pos, list)) {
				ret.add(new Triangle(curVec, neVec, prVec));
				list.remove(curVer);
				curVer = getfar(list);
			} else {
				curVer = curVer == list.size() - 1 ? 0 : curVer + 1;
			}
		}
		return ret;
	}

	public static boolean includePoint(Vec3d curVec, Vec3d neVec, Vec3d prVec, List<Vertex> list) {
		int curVer = getfar(list);
		int ne = curVer == list.size() - 1 ? 0 : curVer + 1;
		int pr = curVer == 0 ? list.size() - 1 : curVer - 1;
		Vec3d curcross = MitoMath.getNormal(curVec, neVec, prVec);
		boolean flag = false;

		for (int n = 0; n < list.size(); n++) {
			if (n != curVer && n != ne && n != pr) {
				Vec3d d1 = list.get(n).pos;
				//if (Math.abs(curcross.dotProduct(MitoMath.vectorSub(d1, curVec))) < 1.0) {
				Vec3d v12 = MitoMath.sub_vector(neVec, curVec);
				Vec3d v13 = MitoMath.sub_vector(prVec, curVec);
				Vec3d vn1 = MitoMath.sub_vector(v12, MitoMath.vectorMul(v13.normalize(), v13.normalize().dotProduct(v12)));
				if (vn1.dotProduct(MitoMath.sub_vector(d1, curVec)) > 0) {
					Vec3d vn2 = MitoMath.sub_vector(v13, MitoMath.vectorMul(v12.normalize(), v12.normalize().dotProduct(v13)));
					if (vn2.dotProduct(MitoMath.sub_vector(d1, curVec)) > 0) {
						Vec3d v32 = MitoMath.sub_vector(neVec, prVec);
						Vec3d v31 = MitoMath.sub_vector(curVec, prVec);
						Vec3d vn3 = MitoMath.sub_vector(v31, MitoMath.vectorMul(v32.normalize(), v32.normalize().dotProduct(v31)));
						if (vn3.dotProduct(MitoMath.sub_vector(d1, prVec)) > 0) {
							flag = true;
							break;
						}
					}
				}
				//}
			}
		}
		return flag;
	}

	public static int getfar(List<Vertex> line) {
		double d = 0;
		int ret = 0;
		for (int n = 0; n < line.size(); n++) {
			double d1 = MitoMath.abs2(line.get(n).pos);
			if (d1 > d) {
				d = d1;
				ret = n;
			}
		}
		return ret;
	}

	public static void rotation(int side) {
		if (side == 1) {
			GL11.glRotatef(180, 1, 0, 0);
		} else if (side == 2) {
			GL11.glRotatef(90, 1, 0, 0);
		} else if (side == 3) {
			GL11.glRotatef(270, 1, 0, 0);
		} else if (side == 4) {
			GL11.glRotatef(270, 0, 0, 1);
		} else if (side == 5) {
			GL11.glRotatef(90, 0, 0, 1);
		}
	}

	public static AxisAlignedBB createAABBByVec3d(Vec3d set, Vec3d end) {
		double minX, minY, minZ, maxX, maxY, maxZ;
		if (set.xCoord > end.xCoord) {
			maxX = set.xCoord;
			minX = end.xCoord;
		} else {
			maxX = end.xCoord;
			minX = set.xCoord;
		}
		if (set.yCoord > end.yCoord) {
			maxY = set.yCoord;
			minY = end.yCoord;
		} else {
			maxY = end.yCoord;
			minY = set.yCoord;
		}
		if (set.zCoord > end.zCoord) {
			maxZ = set.zCoord;
			minZ = end.zCoord;
		} else {
			maxZ = end.zCoord;
			minZ = set.zCoord;
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/*public static BB_RayTraceResult rayTraceBrace(EntityLivingBase player, Vec3d set, Vec3d end, double partialticks) {

		World world = player.worldObj;
		BB_RayTraceResult m = null;
		List list = ChunkWorldManager.getWorldData(world).getBraceBaseWithAABB(MitoUtil.createAABBByVec3d(set, end));

		double l = 999.0D;
		for (int n = 0; n < list.size(); n++) {
			if (list.get(n) instanceof BraceBase) {
				BraceBase base = (BraceBase) list.get(n);
				Line line = base.interactWithRay(set, end);
				if (line != null) {
					double l2 = MitoMath.subAbs(line.start, set);
					if (l2 < l) {
						l = l2;
						m = new BB_RayTraceResult(base, line.end);
					}
				}
			}
		}

		return m;
	}

	public static RayTraceResult rayTraceIncl(EntityLivingBase player, double distance, double partialticks) {

		Vec3d start = MitoUtil.getPlayerEyePosition(player, partialticks);
		Vec3d vec31 = player.getLook((float) partialticks);
		Vec3d end = start.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);

		RayTraceResult pre = player.worldObj.func_147447_a(MitoMath.copyVec3d(start), end, false, false, true);
		RayTraceResult m1 = pre == null ? null : new BB_RayTraceResult(pre);
		RayTraceResult m2 = MitoUtil.rayTraceBrace(player, start, end, (float) partialticks);

		RayTraceResult mop;

		if (m1 == null && m2 == null) {
			return null;
		} else if (m1 == null) {
			mop = m2;
		} else if (m2 == null) {
			mop = m1;
		} else {
			if (MitoMath.subAbs(start, m1.hitVec) + 0.05 < MitoMath.subAbs(start, m2.hitVec) && !(player.worldObj.isAirBlock(m1.blockX, m1.blockY, m1.blockZ))) {
				mop = m1;
			} else {
				mop = m2;
			}
		}

		return mop;
	}*/

	public static boolean canClick(World world, BB_Key key, RayTraceResult mop) {
		switch (mop.typeOfHit) {
			case BLOCK:
				return !world.isAirBlock(mop.getBlockPos()) || key.isAltPressed();
			case ENTITY:
				return true;
			case MISS:
				return key.isAltPressed();
			default:
				return true;
		}
	}

	public static Vec3d getPlayerEyePosition(EntityLivingBase player, double partialticks) {
		Vec3d set;
		if (partialticks == 1.0F) {
			set = new Vec3d(player.posX, player.posY, player.posZ);
		} else {
			double d0 = player.prevPosX + (player.posX - player.prevPosX) * partialticks;
			double d1 = player.prevPosY + (player.posY - player.prevPosY) * partialticks;
			double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * partialticks;
			set = new Vec3d(d0, d1, d2);
		}
		if (!player.worldObj.isRemote) {
			set = set.addVector(0, 1.62, 0);
		}
		return set;
	}

	public static Vec3d conversionByControlKey(EntityPlayer player, Vec3d set) {

		Vec3d ret = new Vec3d(set.xCoord, set.yCoord, set.zCoord);

		Vec3d vec31 = new Vec3d(Math.floor(ret.xCoord * 2 + 0.5) / 2, Math.floor(ret.yCoord * 2 + 0.5) / 2, Math.floor(ret.zCoord * 2 + 0.5) / 2);

		ret = vec31;

		return ret;

	}

	public static Vec3d snapByShiftKey(EntityPlayer player, Vec3d end, ItemStack itemstack) {

		Vec3d ret = new Vec3d(end.xCoord, end.yCoord, end.zCoord);

		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null) {
			Vec3d set = new Vec3d(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));

			if (Math.abs(ret.xCoord - set.xCoord) > Math.abs(ret.yCoord - set.yCoord) && Math.abs(ret.xCoord - set.xCoord) > Math.abs(ret.zCoord - set.zCoord)) {
				ret = new Vec3d(ret.xCoord, set.yCoord, set.zCoord);
			} else if (Math.abs(ret.yCoord - set.yCoord) > Math.abs(ret.zCoord - set.zCoord)) {
				ret = new Vec3d(set.xCoord, ret.yCoord, set.zCoord);
			} else {
				ret = new Vec3d(set.xCoord, set.yCoord, ret.zCoord);
			}
		}

		return ret;
	}

	public static void snapByShiftKey(RayTraceResult mop, Vec3d set) {
		Vec3d ret = new Vec3d(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);

		if (Math.abs(mop.hitVec.xCoord - set.xCoord) > Math.abs(mop.hitVec.yCoord - set.yCoord) && Math.abs(mop.hitVec.xCoord - set.xCoord) > Math.abs(mop.hitVec.zCoord - set.zCoord)) {
			ret = new Vec3d(ret.xCoord, set.yCoord, set.zCoord);
		} else if (Math.abs(mop.hitVec.yCoord - set.yCoord) > Math.abs(mop.hitVec.zCoord - set.zCoord)) {
			ret = new Vec3d(set.xCoord, ret.yCoord, set.zCoord);
		} else {
			ret = new Vec3d(set.xCoord, set.yCoord, ret.zCoord);
		}
		mop.hitVec = ret;
	}

	public static AxisAlignedBB createAabb(double x, double y, double z, double width, double height, double depth) {
		return new AxisAlignedBB(x - width / 2, y - height / 2, z - depth / 2, x + width / 2, y + height / 2, z + depth / 2);
	}

	public static AxisAlignedBB createAabb(Vec3d v, double width, double height, double depth) {
		return createAabb(v.xCoord, v.yCoord, v.zCoord, width, height, depth);
	}

	public static AxisAlignedBB createAabbBySize(double x, double y, double z, double s) {
		double size = s / 2;
		return new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);
	}

	public static AxisAlignedBB createAabbBySize(Vec3d v, double s) {
		return createAabbBySize(v.xCoord, v.yCoord, v.zCoord, s);
	}

	public static boolean isVecEqual(Vec3d v1, Vec3d v2) {
		return MitoMath.subAbs2(v1, v2) < 0.0001;
	}

	public static void snapBlock(RayTraceResult mop) {
		EnumFacing side = mop.sideHit;
		double x = side.getAxis() == EnumFacing.Axis.X ? mop.hitVec.xCoord : Math.floor(mop.hitVec.xCoord * 2 + 0.5) / 2;
		double y = side.getAxis() == EnumFacing.Axis.Y ? mop.hitVec.yCoord : Math.floor(mop.hitVec.yCoord * 2 + 0.5) / 2;
		double z = side.getAxis() == EnumFacing.Axis.Z ? mop.hitVec.zCoord : Math.floor(mop.hitVec.zCoord * 2 + 0.5) / 2;
		mop.hitVec = new Vec3d(x, y, z);
	}

	/*public static List<int[]> getBlocksOnLine(World world, Vec3d set, Vec3d end, ItemStack itemStack, double size) {
		return getBlocksOnLine(world, set, end, itemStack, size, false);
	}

	public static List<int[]> getBlocksOnLine(World world, Vec3d vec1, Vec3d vec2, ItemStack itemStack, double size, boolean replace) {

		List<int[]> ret = new ArrayList<int[]>();

		Vec3d set = MitoMath.copyVec3d(vec1);
		Vec3d end = MitoMath.copyVec3d(vec2);

		int i = MathHelper.floor_double(end.xCoord);
		int j = MathHelper.floor_double(end.yCoord);
		int k = MathHelper.floor_double(end.zCoord);
		int l = MathHelper.floor_double(set.xCoord);
		int i1 = MathHelper.floor_double(set.yCoord);
		int j1 = MathHelper.floor_double(set.zCoord);

		if (world.getBlock(l, i1, j1).canReplace(world, l, i1, j1, world.getBlockMetadata(l, i1, j1), itemStack)) {
			ret.add(new int[] { l, i1, j1 });
		}

		int k1 = 200;

		while (k1-- >= 0) {
			if (Double.isNaN(set.xCoord) || Double.isNaN(set.yCoord) || Double.isNaN(set.zCoord)) {
				return null;
			}

			if (l == i && i1 == j && j1 == k) {
				return ret;
			}

			boolean flag6 = true;
			boolean flag3 = true;
			boolean flag4 = true;
			double d0 = 999.0D;
			double d1 = 999.0D;
			double d2 = 999.0D;

			if (i > l) {
				d0 = (double) l + 1.0D;
			} else if (i < l) {
				d0 = (double) l + 0.0D;
			} else {
				flag6 = false;
			}

			if (j > i1) {
				d1 = (double) i1 + 1.0D;
			} else if (j < i1) {
				d1 = (double) i1 + 0.0D;
			} else {
				flag3 = false;
			}

			if (k > j1) {
				d2 = (double) j1 + 1.0D;
			} else if (k < j1) {
				d2 = (double) j1 + 0.0D;
			} else {
				flag4 = false;
			}

			double d3 = 999.0D;
			double d4 = 999.0D;
			double d5 = 999.0D;
			double d6 = end.xCoord - set.xCoord;
			double d7 = end.yCoord - set.yCoord;
			double d8 = end.zCoord - set.zCoord;

			if (flag6) {
				d3 = (d0 - set.xCoord) / d6;
			}

			if (flag3) {
				d4 = (d1 - set.yCoord) / d7;
			}

			if (flag4) {
				d5 = (d2 - set.zCoord) / d8;
			}

			boolean flag5 = false;
			byte b0;

			if (d3 < d4 && d3 < d5) {
				if (i > l) {
					b0 = 4;
				} else {
					b0 = 5;
				}

				set.xCoord = d0;
				set.yCoord += d7 * d3;
				set.zCoord += d8 * d3;
			} else if (d4 < d5) {
				if (j > i1) {
					b0 = 0;
				} else {
					b0 = 1;
				}

				set.xCoord += d6 * d4;
				set.yCoord = d1;
				set.zCoord += d8 * d4;
			} else {
				if (k > j1) {
					b0 = 2;
				} else {
					b0 = 3;
				}

				set.xCoord += d6 * d5;
				set.yCoord += d7 * d5;
				set.zCoord = d2;
			}

			Vec3d vec32 = new Vec3d(set.xCoord, set.yCoord, set.zCoord);
			l = (int) (vec32.xCoord = (double) MathHelper.floor_double(set.xCoord));

			if (b0 == 5) {
				--l;
				++vec32.xCoord;
			}

			i1 = (int) (vec32.yCoord = (double) MathHelper.floor_double(set.yCoord));

			if (b0 == 1) {
				--i1;
				++vec32.yCoord;
			}

			j1 = (int) (vec32.zCoord = (double) MathHelper.floor_double(set.zCoord));

			if (b0 == 3) {
				--j1;
				++vec32.zCoord;
			}

			if (replace || world.getBlock(l, i1, j1).canReplace(world, l, i1, j1, world.getBlockMetadata(l, i1, j1), itemStack)) {
				double res = size / 2;
				if (new AxisAlignedBB(l + 0.5 - res, i1 + 0.5 - res, j1 + 0.5 - res, l + 0.5 + res, i1 + 0.5 + res, j1 + 0.5 + res).calculateIntercept(set, end) != null)
					ret.add(new int[] { l, i1, j1 });
			}
		}

		return ret;
	}*/

	public static boolean isBrace(RayTraceResult movingOP) {
		return movingOP.typeOfHit == RayTraceResult.Type.ENTITY && movingOP.entityHit != null && movingOP.entityHit instanceof EntityWrapper;
	}

	public static ExtraObject getBrace(RayTraceResult target) {
		if (target.entityHit != null && target.entityHit instanceof EntityWrapper)
			return ((EntityWrapper) target.entityHit).base;
		return null;
	}

	public static void snapBlockOffset(RayTraceResult mop) {
		if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			int x = mop.sideHit.getFrontOffsetX();
			int y = mop.sideHit.getFrontOffsetY();
			int z = mop.sideHit.getFrontOffsetZ();
			if (x != 0) {
				mop.hitVec = new Vec3d(0.5 + (double) mop.getBlockPos().getX() + (double) x, mop.hitVec.yCoord, mop.hitVec.zCoord);
			} else if (y != 0) {
				mop.hitVec = new Vec3d(mop.hitVec.xCoord, 0.5 + (double) mop.getBlockPos().getY() + (double) y, mop.hitVec.zCoord);
			} else {
				mop.hitVec = new Vec3d(mop.hitVec.xCoord, mop.hitVec.yCoord, 0.5 + (double) mop.getBlockPos().getZ() + (double) z);
			}
		}
	}

	public static List<ExtraObject> copyList(List<ExtraObject> list, Vec3d pos) {
		List<ExtraObject> ret = new ArrayList<ExtraObject>();
		Vec3d pos1 = MitoMath.vectorMul(pos, -1);
		for (ExtraObject eo : list) {
			ExtraObject eo1 = eo.copy();
			eo1.translate(pos1);
			ret.add(eo1);
		}
		return ret;
	}

	public static List<Triangle> decomposeTexture(List<Triangle> triangles) {
		List<Triangle> list = new ArrayList<Triangle>();
		for (Triangle t : triangles) {
			decomposeTexture(new BB_Polygon(t), list);
		}
		return list;
	}

	public static List<Triangle> decomposeTexture(BB_Polygon t) {
		List<Triangle> list = new ArrayList<Triangle>();
		decomposeTexture(t, list);
		return list;
	}

	public static void decomposeTexture(BB_Polygon p, List<Triangle> list) {
		List<BB_Polygon> polys = new ArrayList<BB_Polygon>();
		polys.add(p);
		for (int lineU = (int) Math.floor(minU(p)) + 1; lineU <= maxU(p); lineU++) {
			List<BB_Polygon> polys2 = new ArrayList<BB_Polygon>();
			for (BB_Polygon p1 : polys) {
				decomposeLineU(p1, lineU, polys2);
			}
			polys = polys2;
		}
		for (int lineV = (int) Math.floor(minV(p)) + 1; lineV <= maxV(p); lineV++) {
			List<BB_Polygon> polys2 = new ArrayList<BB_Polygon>();
			for (BB_Polygon p1 : polys) {
				decomposeLineV(p1, lineV, polys2);
			}
			polys = polys2;
		}
		for (BB_Polygon p1 : polys) {
			list.addAll(MyUtil.decomposePolygon(p1.getLine()));
		}
	}

	private static void decomposeLineV(BB_Polygon p, int v, List<BB_Polygon> ap) {
		int n1 = -1;
		int n2 = -1;
		double d1 = 0;
		double d2 = 0;
		boolean flag = true;
		if (v < maxV(p) && v > minV(p)) {
			for (int n = 0; n < p.getLine().size(); n++) {
				Vertex v1 = p.getLine().get(n);
				Vertex v2 = p.getLine().get((n + 1) % p.getLine().size());
				if (v1.v <= v == v2.v > v) {
					if (flag) {
						n1 = n;
						d1 = (v - v1.v) / (v2.v - v1.v);
						flag = false;
					} else {
						n2 = n;
						d2 = (v - v1.v) / (v2.v - v1.v);
						continue;
					}
				}
			}
			if (n1 != -1 && n2 != -1) {

				Vertex v1 = splitVertex(p.getLine().get(n1), p.getLine().get((n1 + 1) % p.getLine().size()), d1);
				Vertex v2 = splitVertex(p.getLine().get(n2), p.getLine().get((n2 + 1) % p.getLine().size()), d2);
				BB_Polygon p1 = new BB_Polygon(), p2 = new BB_Polygon();
				for (int i = 0; i < p.getLine().size(); i++) {
					p1.getLine().add(p.getLine().get(i));
					if (i == n1) {
						if (p.getLine().get(i).v != v1.v)
							p1.getLine().add(v1.copy());
						p1.getLine().add(v2.copy());
						i = n2;
					}
				}
				for (int i = n1 + 1; i < n2 + 1; i++) {
					p2.getLine().add(p.getLine().get(i));
					if (i == n2) {
						if (p.getLine().get(i).v != v2.v)
							p2.getLine().add(v2.copy());
						p2.getLine().add(v1.copy());
					}
				}
				ap.add(p1);
				ap.add(p2);
				return;
			}
		}
		ap.add(p);
	}

	private static void decomposeLineU(BB_Polygon p, int u, List<BB_Polygon> ap) {
		int n1 = -1;
		int n2 = -1;
		double d1 = 0;
		double d2 = 0;
		boolean flag = true;
		if (u < maxU(p) && u > minU(p)) {
			for (int n = 0; n < p.getLine().size(); n++) {
				Vertex v1 = p.getLine().get(n);
				Vertex v2 = p.getLine().get((n + 1) % p.getLine().size());
				if (v1.u <= u == v2.u > u) {
					if (flag) {
						n1 = n;
						d1 = (u - v1.u) / (v2.u - v1.u);
						flag = false;
					} else {
						n2 = n;
						d2 = (u - v1.u) / (v2.u - v1.u);
						continue;
					}
				}
			}
			if (n1 != -1 && n2 != -1) {

				Vertex v1 = splitVertex(p.getLine().get(n1), p.getLine().get((n1 + 1) % p.getLine().size()), d1);
				Vertex v2 = splitVertex(p.getLine().get(n2), p.getLine().get((n2 + 1) % p.getLine().size()), d2);
				BB_Polygon p1 = new BB_Polygon(), p2 = new BB_Polygon();
				for (int i = 0; i < p.getLine().size(); i++) {
					p1.getLine().add(p.getLine().get(i));
					if (i == n1) {
						if (p.getLine().get(i).u != v1.u)
							p1.getLine().add(v1.copy());
						p1.getLine().add(v2.copy());
						i = n2;
					}
				}
				for (int i = n1 + 1; i < n2 + 1; i++) {
					p2.getLine().add(p.getLine().get(i));
					if (i == n2) {
						if (p.getLine().get(i).u != v2.u)
							p2.getLine().add(v2.copy());
						p2.getLine().add(v1.copy());
					}
				}
				ap.add(p1);
				ap.add(p2);
				return;
			}
		}
		ap.add(p);
	}

	public static double maxU(BB_Polygon p) {
		double maxu = Double.MIN_VALUE;
		for (Vertex v : p.getLine()) {
			if (v.u > maxu) {
				maxu = v.u;
			}
		}
		return maxu;
	}

	public static double maxV(BB_Polygon p) {
		double maxv = Double.MIN_VALUE;
		for (Vertex v : p.getLine()) {
			if (v.v > maxv) {
				maxv = v.v;
			}
		}
		return maxv;
	}

	public static double minU(BB_Polygon p) {
		double maxu = Double.MAX_VALUE;
		for (Vertex v : p.getLine()) {
			if (v.u < maxu) {
				maxu = v.u;
			}
		}
		return maxu;
	}

	public static double minV(BB_Polygon p) {
		double maxu = Double.MAX_VALUE;
		for (Vertex v : p.getLine()) {
			if (v.v < maxu) {
				maxu = v.v;
			}
		}
		return maxu;
	}

	private static Vertex splitVertex(Vertex v1, Vertex v2, double d) {
		return new Vertex(MitoMath.ratio_vector(v1.pos, v2.pos, d), v1.u + d * (v2.u - v1.u), v1.v + d * (v2.v - v1.v), MitoMath.ratio_vector(v1.norm, v2.norm, d).normalize());
	}

	/*public static List<Triangle> decomposeTexture(List<Triangle> list) {
		List<Triangle> list1 = new ArrayList<Triangle>();
		for (int n = 0; n < list.size(); n++) {
			decomposeTexture(list.get(n), list1);
		}
		return list1;
	}

	public static void decomposeTexture(Triangle tri, List<Triangle> list) {
		double maxU = Double.MIN_VALUE;
		double minU = Double.MAX_VALUE;
		double maxV = Double.MIN_VALUE;
		double minV = Double.MAX_VALUE;
		for (int n1 = 0; n1 < 3; n1++) {
			if (tri.vertexs[n1].textureU > maxU) {
				maxU = tri.vertexs[n1].textureU;
			}
			if (tri.vertexs[n1].textureU < minU) {
				minU = tri.vertexs[n1].textureU;
			}
			if (tri.vertexs[n1].textureV > maxV) {
				maxV = tri.vertexs[n1].textureV;
			}
			if (tri.vertexs[n1].textureV < minV) {
				minV = tri.vertexs[n1].textureV;
			}
		}
		for (int lineU = (int) Math.floor(minU); (double) lineU < maxU; lineU++) {
			for (int lineV = (int) Math.floor(minV); (double) lineV < maxV; lineV++) {
				decomposeSquare(tri, list, lineU, lineV);
			}
		}
	}

	private static void decomposeSquare(Triangle tri, List<Triangle> list, int u, int v) {


	}

	public static BB_Polygon SplitPolygon(BB_Polygon po, Vertex v1, Vertex v2){
		return po;
	}*/

	public static Vec3d snapPoints(Vec3d v, List<Vec3d> points, double r){
		double d = Double.MAX_VALUE;
		for(Vec3d p : points){
			double d1 = p.distanceTo(v);
			if(d1 < r && d1 < d){
				d = d1;
				v = p;
			}
		}
		return v;
	}

}
