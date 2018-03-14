package com.mito.exobj.module.exobject;

import com.mito.exobj.lib.render.model.Line;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GuideBrace extends Brace {


	public String name;

	public GuideBrace(World world) {
		super(world);
	}

	public GuideBrace(World world, Vec3d pos) {
		super(world, pos);
	}

	public GuideBrace(World world, Vec3d pos, Vec3d end, double size, EntityPlayer player) {
		this(world, pos);
		this.line = new Line(pos, end);
		this.size = size;
		name = player.getDisplayNameString();
	}

	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		//this.line.readNBT(nbt);

		Vec3d start = getVec3(nbt, "start");
		Vec3d end = getVec3(nbt, "end");
		line = new Line(start, end);
		this.size = nbt.getDouble("size");

		name = nbt.getString("player");
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		if (line != null) {
			line.writeNBT(nbt);
			nbt.setDouble("size", this.size);
		}
		nbt.setString("player", name);
	}

}
