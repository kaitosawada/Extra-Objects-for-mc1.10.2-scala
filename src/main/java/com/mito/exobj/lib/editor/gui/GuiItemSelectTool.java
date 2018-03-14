package com.mito.exobj.lib.editor.gui;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.editor.BB_SelectedGroup;
import com.mito.exobj.module.main.Main;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiItemSelectTool extends GuiScreen {

	private static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/gui/transform.png");
	private BB_SelectedGroup sel = Main.proxy.sg;
	protected int xSize = 176;
	protected int ySize = 166;

	public GuiItemSelectTool() {
		this.xSize = 256;
		this.ySize = 95;
	}

	@Override
	public void initGui() {
		super.initGui();
		//make buttons
		//id, x, y, width, height, text
		GuiButton button = new GuiButton(1, this.width / 2 - 120, this.height / 2 + 60, 30, 20, I18n.format("gui.back"));
		button.enabled = false;
		buttonList.add(button);
		buttonList.add(new GuiButton(2, this.width / 2 - 120, this.height / 2 + 82, 30, 20, I18n.format("gui.done")));

		buttonList.add(new GuiButton(101, this.width / 2 - 80, this.height / 2 + 60, 30, 20, "copy"));
		buttonList.add(new GuiButton(102, this.width / 2 - 80, this.height / 2 + 82, 30, 20, "move"));
		buttonList.add(new GuiButton(103, this.width / 2 - 80, this.height / 2 + 104, 30, 20, "delete"));

		buttonList.add(new GuiButton(201, this.width / 2 - 40, this.height / 2 + 60, 30, 20, "property"));
		buttonList.add(new GuiButton(202, this.width / 2 - 40, this.height / 2 + 82, 30, 20, "transform"));

		buttonList.add(new GuiButton(301, this.width / 2, this.height / 2 + 60, 30, 20, "group"));
		buttonList.add(new GuiButton(302, this.width / 2, this.height / 2 + 82, 30, 20, "block"));
		buttonList.add(new GuiButton(303, this.width / 2, this.height / 2 + 104, 30, 20, ".obj"));

		//		this.buttonList.add(new GuiButton(100, this.width / 2 - 120, this.height / 2 + 10, 20, 20, "<"));
		//		this.buttonList.add(new GuiButton(101, this.width / 2 - 60, this.height / 2 + 10, 20, 20, ">"));
		//		this.buttonList.add(new GuiButton(102, this.width / 2 - 120, this.height / 2 + 30, 20, 20, "<"));
		//		this.buttonList.add(new GuiButton(103, this.width / 2 - 60, this.height / 2 + 30, 20, 20, ">"));
		//		this.buttonList.add(new GuiButton(104, this.width / 2 - 120, this.height / 2 + 50, 20, 20, "<"));
		//		this.buttonList.add(new GuiButton(105, this.width / 2 - 60, this.height / 2 + 50, 20, 20, ">"));
		//this.move[0] = this.setTextField(30, 10, 40, 20, String.valueOf(0));
		//this.move[1] = this.setTextField(30, 30, 40, 20, String.valueOf(sel.getPos(true)[1]));
		//this.move[2] = this.setTextField(30, 50, 40, 20, String.valueOf(sel.getPos(true)[2]));
	}

	protected void actionPerformed(GuiButton guibutton) {
		//id is the id you give your button
		switch (guibutton.id) {
			case 1:
				break;
			case 2:
				this.mc.thePlayer.closeScreen();
				break;
			case 101:
				sel.mode = BB_SelectedGroup.SelectMode.Copy;
				this.mc.thePlayer.closeScreen();
				break;
			case 102:
				sel.mode = BB_SelectedGroup.SelectMode.Move;
				this.mc.thePlayer.closeScreen();
				break;
			case 103:
				sel.breakGroup();
				sel.delete();
				this.mc.thePlayer.closeScreen();
				break;
			case 201:
				this.mc.displayGuiScreen(new GuiBraceProperty());
				break;
			case 202:
				this.mc.displayGuiScreen(new GuiGroupTransform());
				break;
			case 301:
				//sel.grouping();
				this.mc.thePlayer.closeScreen();
				break;
			case 302:
				sel.mode = BB_SelectedGroup.SelectMode.Block;
				this.mc.thePlayer.closeScreen();
				break;
			case 303:
				sel.outputObj();
				this.mc.thePlayer.closeScreen();
				break;
		}
		//Packet code here
		//PacketDispatcher.sendPacketToServer(packet); //send packet
	}

	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		FontRenderer fontrenderer = this.fontRendererObj;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2 + 100;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}

	protected void keyTyped(char p_73869_1_, int p_73869_2_) {
		if (p_73869_2_ == 1 || p_73869_2_ == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawDefaultBackground() {
	}

}
