package com.mito.exobj.lib.item;

import java.util.List;

import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.editor.BB_SelectedGroup;
import com.mito.exobj.lib.network.EditerPacketProcessor;
import com.mito.exobj.lib.render.RenderHighLight;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.network.EditerPacketProcessor.EnumGroupMode;
import com.mito.exobj.lib.network.PacketHandler;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.mito.exobj.lib.editor.BB_SelectedGroup.SelectMode.*;

public class ItemSelectTool extends ItemBraceBase implements IMouseWheel, IHighLight {

	public ItemSelectTool() {
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, RayTraceResult mop, BB_Key key) {
		if (world.isRemote) {
			BB_SelectedGroup sel = Main.proxy.sg;

			//移動モードのとき
			switch (sel.mode) {
				case Copy:
					//シフトクリックでモード解除
					if (key.isShiftPressed() || key.isControlPressed()) {
						sel.delete();
						//コピー処理
					} else {
						if (!sel.getList().isEmpty()) {
							Vec3d pos = sel.getDistance(mop);
							double yaw = 0;
							PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.COPY, sel.getList(), pos, yaw));
						}
					}
					if (!key.isAltPressed()) {
						sel.mode = None;
						sel.activated = false;
					}
					break;
				case Move:
					//シフトクリックでモード解除
					if (key.isShiftPressed() || key.isControlPressed()) {
						sel.delete();
						//移動処理
					} else {
						if (!sel.getList().isEmpty()) {
							Vec3d pos = sel.getDistance(mop);
							double yaw = 0;
							PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.COPY, sel.getList(), pos, yaw));
							sel.breakGroup();
							sel.delete();
						}
					}
					sel.mode = None;
					sel.activated = false;
					break;
				case None:
					//矩形セレクト
					if (sel.activated) {
						Vec3d set = mop.hitVec;
						if (MitoMath.subAbs(sel.set, set) < 500000) {
							AxisAlignedBB aabb = MyUtil.createAABBByVec3d(sel.set, set);
							List<ExtraObject> list = ChunkWorldManager.getWorldData(world).getExtraObjectWithAABB(aabb);
							//シフト同時押しでセレクト追加
							if (key.isShiftPressed() || key.isControlPressed()) {
								sel.addShift(list);
							} else {
								sel.replace(list);
							}
						}
						sel.activated = false;
						//直接セレクト
					} else {
						if (MyUtil.isBrace(mop)) {
							ExtraObject base = ((EntityWrapper) mop.entityHit).base;
							//シフト同時押しでセレクト追加
							if (key.isShiftPressed() || key.isControlPressed()) {
								sel.addShift(base);
								//GUIを開く
							} else {
								if (sel.getList().contains(base)) {
									//GUI
									if (key.isAltPressed()) {
										sel.set = mop.hitVec;
										sel.mode = Copy;
										//PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.COPY, sel.getList()));
									} else {
										sel.set = mop.hitVec;
										PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.GUI));
									}
								} else {
									sel.replace(base);
								}
							}
							sel.activated = false;
							//ブレース以外をクリック
						} else {
							if (key.isShiftPressed() || key.isControlPressed()) {
								sel.delete();
							} else {
								Vec3d set = mop.hitVec;
								sel.set = set;
								sel.activated = true;
							}
						}
					}
					break;
				case Block:
					//解除
					if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
						if (key.isShiftPressed() || key.isControlPressed()) {
							sel.delete();
							//ブロック化処理
						} else {
							int x = mop.sideHit.getFrontOffsetX();
							int y = mop.sideHit.getFrontOffsetY();
							int z = mop.sideHit.getFrontOffsetZ();
							Vec3d v = new Vec3d(mop.getBlockPos().getX() + x, mop.getBlockPos().getY() + y, mop.getBlockPos().getZ() + z);
							PacketHandler.INSTANCE.sendToServer(new EditerPacketProcessor(EnumGroupMode.SETBLOCK, sel.getList(), v));
							//sel.breakGroup();
							sel.delete();
						}
						sel.mode = None;
						sel.activated = false;
					}
					break;
			}
		}
	}


	@Override
	public boolean drawHighLight(ItemStack itemStack, EntityPlayer player, float partialticks, RayTraceResult mop) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			BB_SelectedGroup sel = Main.proxy.sg;
			if (mop == null)
				return false;
			Vec3d set = mop.hitVec;
			RenderHighLight rh = RenderHighLight.INSTANCE;
			if (sel.mode == Copy || sel.mode == Move) {
				sel.drawHighLightCopy(player, partialticks, mop);
			} else if (sel.mode == Block && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				int x = mop.sideHit.getFrontOffsetX();
				int y = mop.sideHit.getFrontOffsetY();
				int z = mop.sideHit.getFrontOffsetZ();
				Vec3d v = new Vec3d(0.5 + mop.getBlockPos().getX() + x, 0.5 + mop.getBlockPos().getY() + y, 0.5 + mop.getBlockPos().getZ() + z);
				rh.drawBox(player, v, 0.95, partialticks);
			}
			sel.drawHighLightGroup(player, partialticks);
			if (sel.activated && MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop)) {
				Vec3d end = sel.set;
				rh.drawBox(player, set, end, partialticks);
				return true;
			} else {
				return this.drawHighLightBrace(player, partialticks, mop);
			}
		}
		return false;
	}

	@Override
	public boolean wheelEvent(EntityPlayer player, ItemStack stack, RayTraceResult mop, BB_Key key, int dwheel) {
		/*BB_SelectedGroup sel = Main.proxy.sg;
		if (sel.mode == Copy && key.isShiftPressed()) {
			//int w = dwheel / 120;
			//double div = sel.pasteNum + w;
			if (sel.pasteNum < 0) {
				sel.pasteNum = 50000;
			}
			return true;
		}*/
		return false;
	}

	public void snapDegree(RayTraceResult mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
	}
}
