package com.mito.exobj.lib.editor.gui;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.editor.BB_SelectedGroup;
import com.mito.exobj.module.main.Main;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiGroupTransform extends GuiScreen {

	static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/gui/transform.png");
	private BB_SelectedGroup sel = Main.proxy.sg;
	protected int xSize = 176;
	protected int ySize = 166;

	private GuiTextField size;
	private int isize = 100;

	private GuiTextField roll;
	private int iroll = 0;
	private GuiTextField pitch;
	private int ipitch = 0;
	private GuiTextField yaw;
	private int iyaw = 0;

	public static final String[] color_name = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light blue", "magenta", "orange", "white", "none"};

	public GuiGroupTransform() {
		this.xSize = 256;
		this.ySize = 95;
	}

	@Override
	public void initGui() {
		super.initGui();
		//make buttons
		//id, x, y, width, height, text
		buttonList.add(new GuiButton(1, this.width / 2 - 120, this.height / 2 + 60, 30, 20, I18n.format("gui.back")));
		buttonList.add(new GuiButton(2, this.width / 2 - 120, this.height / 2 + 82, 30, 20, I18n.format("gui.done")));

		buttonList.add(new GuiButton(101, this.width / 2 - 80, this.height / 2 + 60, 30, 20, "apply"));
		buttonList.add(new GuiButton(102, this.width / 2 - 80, this.height / 2 + 60, 30, 20, "cancel"));

		this.buttonList.add(new GuiButton(301, this.width / 2 + 40, this.height / 2 + 60, 20, 20, "<"));
		this.buttonList.add(new GuiButton(302, this.width / 2 + 100, this.height / 2 + 60, 20, 20, ">"));
		this.pitch = this.setTextField(this.width / 2 + 62, this.height / 2 + 60, 34, 20, String.valueOf(sel.pitch));
		this.ipitch = sel.pitch;

		this.buttonList.add(new GuiButton(303, this.width / 2 + 40, this.height / 2 + 88, 20, 20, "<"));
		this.buttonList.add(new GuiButton(304, this.width / 2 + 100, this.height / 2 + 88, 20, 20, ">"));
		this.yaw = this.setTextField(this.width / 2  + 62, this.height / 2 + 88, 34, 20, String.valueOf(sel.yaw));
		this.iyaw = sel.yaw;

		this.buttonList.add(new GuiButton(201, this.width / 2 - 40, this.height / 2 + 60, 20, 20, "<"));
		this.buttonList.add(new GuiButton(202, this.width / 2 + 20, this.height / 2 + 60, 20, 20, ">"));
		this.roll = this.setTextField(this.width / 2 - 18, this.height / 2 + 60, 34, 20, String.valueOf(sel.roll));
		this.iroll = sel.roll;

		this.buttonList.add(new GuiButton(203, this.width / 2 - 40, this.height / 2 + 88, 20, 20, "<"));
		this.buttonList.add(new GuiButton(204, this.width / 2 + 20, this.height / 2 + 88, 20, 20, ">"));
		this.size = this.setTextField(this.width / 2 - 18, this.height / 2 + 88, 34, 20, String.valueOf(sel.size));
		this.isize = sel.size;
		/*int is = this.sel.getSize();
		if(is != -1){
			this.isize = is;
			size.setText(String.valueOf(isize));
		}*/
	}

	protected GuiTextField setTextField(int xPos, int yPos, int w, int h, String text) {
		GuiTextField field = new GuiTextField(0, this.fontRendererObj, xPos, yPos, w, h);
		field.setMaxStringLength(32767);
		field.setFocused(false);
		field.setText(text);
		return field;
	}

	protected void actionPerformed(GuiButton guibutton) {
		//id is the id you give your button
		switch (guibutton.id) {
			case 1:
				this.mc.displayGuiScreen(new GuiItemSelectTool());
				break;
			case 2:
				//sel.applyProperty(selectedTex, icolor, shape);
				this.mc.thePlayer.closeScreen();
				break;
			case 101:
				//sel.applyProperty(selectedTex, icolor, shape);
				break;
			case 102:
				//sel.applyProperty(selectedTex, icolor, shape);
				sel.applyGroupSize(100);
				sel.applyGroupRot(0, 0, 0);
				this.mc.displayGuiScreen(new GuiItemSelectTool());
				break;
			case 203:
				this.isize -= 10;
				if (isize <= 0) {
					isize = 10000;
				}
				size.setText(String.valueOf(isize));
				sel.applyGroupSize(isize);
				break;
			case 204:
				this.isize += 10;
				if (isize > 10000) {
					isize = 0;
				}
				size.setText(String.valueOf(isize));
				sel.applyGroupSize(isize);
				break;
			case 201:
				this.iroll -= 15;
				if (iroll < 0) {
					iroll = 345;
				}
				roll.setText(String.valueOf(iroll));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
			case 202:
				this.iroll += 15;
				if (iroll >= 360) {
					iroll = 0;
				}
				roll.setText(String.valueOf(iroll));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
			case 301:
				this.ipitch -= 15;
				if (ipitch < 0) {
					ipitch = 345;
				}
				pitch.setText(String.valueOf(ipitch));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
			case 302:
				this.ipitch += 15;
				if (ipitch >= 360) {
					ipitch = 0;
				}
				pitch.setText(String.valueOf(ipitch));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
			case 303:
				this.iyaw -= 15;
				if (iyaw < 0) {
					iyaw = 345;
				}
				yaw.setText(String.valueOf(iyaw));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
			case 304:
				this.iyaw += 15;
				if (iyaw >= 360) {
					iyaw = 0;
				}
				yaw.setText(String.valueOf(iyaw));
				sel.applyGroupRot(iroll, ipitch, iyaw);
				break;
		}
		//Packet code here
		//PacketDispatcher.sendPacketToServer(packet); //send packet
	}

	public void drawScreen(int var1, int ver2, float ver3) {
		this.drawDefaultBackground();
		FontRenderer fontrenderer = this.fontRendererObj;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2 + 100;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		roll.drawTextBox();
		pitch.drawTextBox();
		yaw.drawTextBox();
		size.drawTextBox();
		this.fontRendererObj.drawString("z", this.width / 2 - 18, this.height / 2 + 52, 0xFFFFFF);
		this.fontRendererObj.drawString("y", this.width / 2 + 62, this.height / 2 + 52, 0xFFFFFF);
		this.fontRendererObj.drawString("x", this.width / 2 + 62, this.height / 2 + 80, 0xFFFFFF);
		this.fontRendererObj.drawString("size", this.width / 2 - 18, this.height / 2 + 80, 0xFFFFFF);
		super.drawScreen(var1, ver2, ver3);
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

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	@Override
	public void drawDefaultBackground() {
	}

}
