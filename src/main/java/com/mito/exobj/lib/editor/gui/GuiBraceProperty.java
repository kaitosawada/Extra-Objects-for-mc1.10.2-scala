package com.mito.exobj.lib.editor.gui;

import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import org.lwjgl.opengl.GL11;

import com.mito.exobj.lib.editor.BB_SelectedGroup;
import com.mito.exobj.module.main.Main;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiBraceProperty extends GuiScreen {

	static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/gui/transform.png");
	private BB_SelectedGroup sel = Main.proxy.sg;
	protected int xSize = 176;
	protected int ySize = 166;

	/*private BB_GuiScrollingList texList;
	private Block selectedTex = null;
	private int itex = 0;*/

	private BB_GuiScrollingList2 shapeList;
	private String shape;
	private int ishape = 0;

	private GuiTextField color;
	private int icolor = 16;

	private GuiTextField size;
	private int isize = 5;

	private GuiTextField roll;
	private int iroll = 0;

	public static final String[] color_name = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light blue", "magenta", "orange", "white", "none"};

	public GuiBraceProperty() {
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
		/*this.texList = new BB_GuiScrollingList(this, BB_EnumTexture.values(), 60);
		this.texList.registerScrollButtons(this.buttonList, 7, 8);*/
		this.shapeList = new BB_GuiScrollingList2(this, BraceTypeRegistry.typeList, 80);
		this.shapeList.registerScrollButtons(this.buttonList, 7, 8);

		this.buttonList.add(new GuiButton(201, this.width / 2 - 40, this.height / 2 + 60, 20, 20, "<"));
		this.buttonList.add(new GuiButton(202, this.width / 2 + 20, this.height / 2 + 60, 20, 20, ">"));
		this.color = this.setTextField(this.width / 2 - 18, this.height / 2 + 60, 34, 20, "none");

		this.buttonList.add(new GuiButton(203, this.width / 2 - 40, this.height / 2 + 88, 20, 20, "<"));
		this.buttonList.add(new GuiButton(204, this.width / 2 + 20, this.height / 2 + 88, 20, 20, ">"));
		this.size = this.setTextField(this.width / 2 - 18, this.height / 2 + 88, 34, 20, "none");

		this.buttonList.add(new GuiButton(301, this.width / 2 + 60, this.height / 2 + 60, 20, 20, "<"));
		this.buttonList.add(new GuiButton(302, this.width / 2 + 120, this.height / 2 + 60, 20, 20, ">"));
		this.roll = this.setTextField(this.width / 2 + 82, this.height / 2 + 60, 34, 20, "none");

		int is = this.sel.getSize();
		if (is != -1) {
			this.isize = is;
			size.setText(String.valueOf(isize));
		}
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
				sel.applyProperty(null, icolor, shape);
				this.mc.thePlayer.closeScreen();
				break;
			case 101:
				sel.applyProperty(null, icolor, shape);
				break;
			case 201:
				this.icolor--;
				if (icolor < 0) {
					icolor = 16;
				}
				color.setText(color_name[icolor]);
				sel.applyColor(icolor);
				break;
			case 202:
				this.icolor++;
				if (icolor > 16) {
					icolor = 0;
				}
				color.setText(color_name[icolor]);
				sel.applyColor(icolor);
				break;
			case 203:
				this.isize--;
				if (isize < 0) {
					isize = 100;
				}
				size.setText(String.valueOf(isize));
				sel.applySize(isize);
				break;
			case 204:
				this.isize++;
				if (isize > 100) {
					isize = 0;
				}
				size.setText(String.valueOf(isize));
				sel.applySize(isize);
				break;
			case 301:
				this.iroll -= 15;
				if (iroll < 0) {
					iroll = 345;
				}
				roll.setText(String.valueOf(iroll));
				sel.applyRoll(iroll);
				break;
			case 302:
				this.iroll += 15;
				if (iroll >= 360) {
					iroll = 0;
				}
				roll.setText(String.valueOf(iroll));
				sel.applyRoll(iroll);
				break;
		}
		//Packet code here
		//PacketDispatcher.sendPacketToServer(packet); //send packet
	}

	public void drawScreen(int var1, int ver2, float ver3) {
		this.drawDefaultBackground();
		//this.texList.drawScreen(var1, ver2, ver3);
		this.shapeList.drawScreen(var1, ver2, ver3);
		FontRenderer fontrenderer = this.fontRendererObj;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2 + 100;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		color.drawTextBox();
		size.drawTextBox();
		roll.drawTextBox();
		this.fontRendererObj.drawString("color", this.width / 2 - 18, this.height / 2 + 52, 0xFFFFFF);
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

	/*public void selectTextureIndex(int var1) {
		this.itex = var1;
		if (var1 >= 0 && var1 < BB_EnumTexture.values().length) {
			this.selectedTex = BB_EnumTexture.values()[itex];
		} else {
			this.selectedTex = null;
		}
	}*/

	public void selectShapeIndex(int var1) {
		this.ishape = var1;
		if (var1 >= 0 && var1 < BraceTypeRegistry.typeList.size()) {
			this.shape = BraceTypeRegistry.typeList.get(ishape);
		} else {
			this.shape = null;
		}
	}

	public boolean shapeIndexSelected(int var1) {
		return var1 == ishape;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	@Override
	public void drawDefaultBackground() {
	}

}
