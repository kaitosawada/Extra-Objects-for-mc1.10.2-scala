package com.mito.exobj.module.main;

import com.mito.exobj.lib.ExtraObject;
import com.mito.exobj.lib.input.BB_Key;
import com.mito.exobj.lib.editor.BB_SelectedGroup;
import com.mito.exobj.lib.editor.TileObjects;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class CommonProxy implements IEventHandler {

	public BB_SelectedGroup sg;

	public CommonProxy() {
	}

	public boolean isControlKeyDown() {
		return false;
	}

	public boolean isShiftKeyDown() {
		return false;
	}

	public boolean isAltKeyDown() {
		return false;
	}

	public World getClientWorld() {
		return null;
	}

	public BB_Key getKey() {
		return new BB_Key(0);
	}

	public void init() {
		GameRegistry.registerTileEntity(TileObjects.class, "TileObjects");
	}

	public void preInit() {
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public abstract void playSound(SoundEvent se, SoundCategory sc, float vol, float pitch, Vec3d pos);

	public void addDiggingEffect(World world, Vec3d center, double d0, double d1, double d2, Block block, int color) {
	}

	public void particle(ExtraObject brace) {
	}
}
