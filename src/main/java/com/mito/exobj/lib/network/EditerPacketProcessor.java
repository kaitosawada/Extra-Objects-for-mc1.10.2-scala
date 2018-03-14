package com.mito.exobj.lib.network;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.data.ChunkWorldManager;
import com.mito.exobj.lib.data.BB_DataWorld;
import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.lib.editor.TileObjects;
import com.mito.exobj.module.main.ResisterItem;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EditerPacketProcessor implements IMessage, IMessageHandler<EditerPacketProcessor, IMessage> {

	public int[] ids;
	public EnumGroupMode mode;
	public Vec3d pos = null;
	public double yaw;

	public EditerPacketProcessor() {
	}

	public EditerPacketProcessor(EnumGroupMode mode, List<ExtraObject> brace) {
		this.ids = new int[brace.size()];
		for (int n = 0; n < brace.size(); n++) {
			ids[n] = brace.get(n).BBID;
		}
		this.mode = mode;
	}

	public EditerPacketProcessor(EnumGroupMode mode, ExtraObject... brace) {
		this.ids = new int[brace.length];
		for (int n = 0; n < brace.length; n++) {
			ids[n] = brace[n].BBID;
		}
		this.mode = mode;
	}

	public EditerPacketProcessor(EnumGroupMode mode, List<ExtraObject> list, Vec3d pos, double yaw) {
		this(mode, list);
		this.pos = pos;
		this.yaw = yaw;
	}

	public EditerPacketProcessor(EnumGroupMode mode, List<ExtraObject> list, Vec3d pos) {
		this(mode, list);
		this.pos = pos;
	}

	@Override
	public IMessage onMessage(EditerPacketProcessor message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = DimensionManager.getWorld(player.dimension);
		BB_DataWorld data = ChunkWorldManager.getWorldData(world);
		List<ExtraObject> list = new ArrayList<ExtraObject>();
		for (int n : message.ids) {
			ExtraObject base = data.getBraceBaseByID(n);
			if (base != null) {
				list.add(base);
			}
		}
		switch (message.mode) {
			case COPY:
				for (ExtraObject base : list) {
					NBTTagCompound nbt = new NBTTagCompound();
					base.writeToNBTOptional(nbt);
					ExtraObject base1 = ExtraObjectRegistry.createExObjFromNBT(nbt, world);
					base1.translate(message.pos);
					base1.addToWorld();
				}
				break;
			case GUI:
				player.openGui(Main.INSTANCE, Main.GUI_ID_BBSelect, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				break;
			case DELETE:
				for (int n = 0; n < list.size(); n++) {
					ExtraObject base = list.get(n);
					base.breakBrace(player);
				}
				break;
			case GROUPING:
			/*GroupObject group = new GroupObject(world, list.get(0).pos, list);
			group.addToWorld();
			for (int n = 0; n < list.size(); n++) {
				ExtraObject base = list.get(n);
				base.breakBrace(player);
			}*/
				break;
			case SETBLOCK:
				int ix = MathHelper.ceiling_double_int(message.pos.xCoord);
				int iy = MathHelper.ceiling_double_int(message.pos.yCoord);
				int iz = MathHelper.ceiling_double_int(message.pos.zCoord);
				world.setBlockState(new BlockPos(ix, iy, iz), ResisterItem.BlockObjects.getDefaultState());
				TileEntity tile = world.getTileEntity(new BlockPos(ix, iy, iz));

				Vec3d v = new Vec3d(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
				if (tile != null && tile instanceof TileObjects) {
					TileObjects to = (TileObjects) tile;
					for(ExtraObject eo : list){
						eo.translate(v);
						eo.removeFromWorld();
					}
					to.list = list;
					to.markDirty();
				}
			default:
				break;
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mode = EnumGroupMode.values()[buf.readInt()];
		if (mode == EnumGroupMode.COPY) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			pos = new Vec3d(x, y, z);
			yaw = buf.readDouble();
		} else if (mode == EnumGroupMode.SETBLOCK) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			pos = new Vec3d(x, y, z);
		}
		this.ids = new int[buf.readInt()];
		for (int n = 0; n < ids.length; n++) {
			ids[n] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mode.ordinal());
		if (mode == EnumGroupMode.COPY) {
			buf.writeDouble(pos.xCoord);
			buf.writeDouble(pos.yCoord);
			buf.writeDouble(pos.zCoord);
			buf.writeDouble(yaw);
		} else if (mode == EnumGroupMode.SETBLOCK) {
			buf.writeDouble(pos.xCoord);
			buf.writeDouble(pos.yCoord);
			buf.writeDouble(pos.zCoord);
		}
		buf.writeInt(ids.length);
		for (int n = 0; n < ids.length; n++) {
			buf.writeInt(ids[n]);
		}
	}

	public enum EnumGroupMode {
		GUI, COPY, DELETE, GROUPING, SETBLOCK
	}

}
