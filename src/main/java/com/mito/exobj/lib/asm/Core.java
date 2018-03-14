package com.mito.exobj.lib.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class Core implements IFMLLoadingPlugin {

	public Core() {
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"com.mito.exobj.lib.asm.Transformer"};
	}

	@Override
	public String getModContainerClass() {
		return "com.mito.exobj.lib.asm.BraceModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
