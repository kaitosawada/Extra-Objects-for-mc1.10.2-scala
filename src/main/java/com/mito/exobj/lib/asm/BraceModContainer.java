package com.mito.exobj.lib.asm;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class BraceModContainer extends DummyModContainer {

	public BraceModContainer() {
		super(new ModMetadata());

		ModMetadata meta = super.getMetadata();
		meta.modId = "ExtraObjectsCore";
		meta.name = "ExtraObjectsCore";
		meta.version = "1.0";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController lc) {
		bus.register(this);
		return true;
	}

}
