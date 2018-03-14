package com.mito.exobj.lib.editor.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class BB_GuiScrollingList extends GuiScrollingList {

	public BB_GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight) {
		super(client, width, height, top, bottom, left, entryHeight);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isSelected(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void drawBackground() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		// TODO Auto-generated method stub

	}

	/*private GuiBraceProperty parent;
	private ArrayList<BB_EnumTexture> texs;

	public BB_GuiScrollingList(GuiBraceProperty parent, BB_EnumTexture[] texs, int listWidth) {
		super(parent.mc, listWidth, parent.height, 32, parent.height - 66 + 4, 10, 35);
		this.parent = parent;
		this.texs = new ArrayList<BB_EnumTexture>();
		for (int i = 0; i < texs.length; i++) {
			this.texs.add(texs[i]);
		}
		this.texs.add(null);
	}

	@Override
	protected int getSize() {
		return texs.size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2) {
		this.parent.selectTextureIndex(var1);
	}

	@Override
	protected boolean isSelected(int var1) {
		return this.parent.textureIndexSelected(var1);
	}

	@Override
	protected void drawBackground() {
		this.parent.drawDefaultBackground();
	}

	@Override
	protected int getContentHeight() {
		return (this.getSize()) * 35 + 1;
	}

	@Override
	protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
		BB_EnumTexture tex = texs.get(listIndex);
		if (tex != null) {
			this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(tex.name(), listWidth - 10), this.left + 3, var3 + 2, 0xFFFFFF);
			this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(tex.getTextureName(0), listWidth - 10), this.left + 3, var3 + 12, 0xCCCCCC);
		} else {
			this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("none", listWidth - 10), this.left + 3, var3 + 2, 0xFFFFFF);
			this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("", listWidth - 10), this.left + 3, var3 + 12, 0xCCCCCC);
		}
		this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(tex.getMetadata() != null ? tex.getMetadata().getChildModCountString() : "Metadata not found", listWidth - 10), this.left + 3, var3 + 22,
				0xCCCCCC);

	}*/

}
