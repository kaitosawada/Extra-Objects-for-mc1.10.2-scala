package com.mito.exobj.lib.editor.gui;

import com.mito.exobj.module.main.Main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == Main.GUI_ID_BBSelect) {
			return new ContainerItemSelectTool(player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == Main.GUI_ID_BBSelect) {
			return new GuiItemSelectTool();
		}
		return null;
	}

}
