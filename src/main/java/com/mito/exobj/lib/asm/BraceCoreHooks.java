package com.mito.exobj.lib.asm;

import java.util.List;

import com.mito.exobj.lib.data.BB_DataChunk;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.utilities.MyLogger;
import com.mito.exobj.lib.render.model.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

public class BraceCoreHooks {

	public static void getCollisionHook(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, @Nullable Entity entity) {
		List<ExtraObject> list = ChunkWorldManager.getWorldData(world).getExtraObjectWithAABB(aabb);
		for (ExtraObject base : list) {
			base.addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity);
		}
	}

	public static int getLightValue(int value, Chunk chunk, EnumSkyBlock sb, BlockPos pos) {
		if (ChunkWorldManager.existChunkData(chunk)) {
			BB_DataChunk data = ChunkWorldManager.getChunkDataNew(chunk);
			for (ExtraObject e : data.braceList) {
				if (e.interactWithAABB(new AxisAlignedBB(pos))) {
					value = 15;
				}
			}
		}
		chunk.setLightFor(sb, pos, value);
		//MyLogger.info("on test asm");
		return value;
	}

	public static void test() {
		MyLogger.info("on test asm");
	}

	public static void rayTrace(float partialticks) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			if (mc.theWorld != null) {
				EntityLivingBase player = mc.thePlayer;
				double d0 = (double) mc.playerController.getBlockReachDistance();
				RayTraceResult pre = mc.objectMouseOver;
				Vec3d start = player.getPositionEyes(partialticks);
				Vec3d Vec3d1 = player.getLook(partialticks);
				Vec3d end = start.addVector(Vec3d1.xCoord * d0, Vec3d1.yCoord * d0, Vec3d1.zCoord * d0);
				RayTraceResult m2 = rayTraceBrace(player, start, end, partialticks);
				if (m2 != null && pre != null) {
					BlockPos blockPos = pre.getBlockPos();
					if (blockPos != null)
						if (!(MitoMath.subAbs(start, pre.hitVec) + 0.05 < MitoMath.subAbs(start, m2.hitVec) && !(player.worldObj.isAirBlock(blockPos)))) {
							mc.objectMouseOver = m2;
						}
				}
			}
		}
	}

	public static RayTraceResult rayTraceBrace(EntityLivingBase player, Vec3d set, Vec3d end, double partialticks) {
		World world = player.worldObj;
		RayTraceResult m = null;
		List list = ChunkWorldManager.getWorldData(world).getExtraObjectWithAABB(MyUtil.createAABBByVec3d(set, end));
		double l = 999.0D;
		for (int n = 0; n < list.size(); n++) {
			if (list.get(n) instanceof ExtraObject) {
				ExtraObject base = (ExtraObject) list.get(n);
				Line line = base.interactWithRay(set, end);
				if (line != null) {
					double l2 = MitoMath.subAbs(line.start, set);
					if (l2 < l) {
						l = l2;
						m = new RayTraceResult(ChunkWorldManager.getWorldData(world).wrapper.wrap(base), line.end);
					}
				}
			}
		}
		return m;
	}
}
