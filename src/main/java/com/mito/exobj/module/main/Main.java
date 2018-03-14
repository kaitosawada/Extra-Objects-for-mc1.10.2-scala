package com.mito.exobj.module.main;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import com.mito.exobj.lib.EventHandler;

import com.mito.exobj.lib.editor.gui.GuiHandler;
import com.mito.exobj.lib.EntityWrapper;
import com.mito.exobj.lib.network.PacketHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions = Main.MOD_ACCEPTED_MC_VERSIONS)
public class Main {

	public static final String MODID = "mito_extra_objects";
	public static final String MODNAME = "Extra Objects";
	public static final String VERSION = "1.0.0";
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.10,1.10.2]";
	public static boolean quality = false;

	public static final CreativeTabs tab = new CreativeTabExO("CreativeTabExObj");

	@Mod.Instance(Main.MODID)
	public static Main INSTANCE;

	@SidedProxy(clientSide = "com.mito.exobj.module.main.ClientProxy", serverSide = "com.mito.exobj.module.main.CommonProxy")
	public static CommonProxy proxy;

	public static final int GUI_ID_BBSelect = 2;

	@SideOnly(Side.CLIENT)
	//public static int RenderType_Objects;
	public EventHandler leh;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		ResisterItem.preinit(event);
		PacketHandler.init();
		quality = ConfigManager.quality();
		if (event.getSide() == Side.CLIENT) {
			FileLoadManager.resisterFiles(event);
		}
	}

	@Mod.EventHandler
	public void Init(FMLInitializationEvent e) {
		resisterEvent();
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		EntityRegistry.registerModEntity(EntityWrapper.class, "Wrapper", 1, this, 512, 120, false);
		ResisterItem.RegisterRecipe();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		/*if (debug) {
			LoadNGTLib.elase();
		}*/
	}

	public void resisterEvent() {
		this.leh = new EventHandler();
		leh.registerEvent();

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		proxy.registerEvent();
	}


}
