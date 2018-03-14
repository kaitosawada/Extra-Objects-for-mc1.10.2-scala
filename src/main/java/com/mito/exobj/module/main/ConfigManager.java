package com.mito.exobj.module.main;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.*;

public class ConfigManager {

	static private Configuration config = null;

	static public Configuration getConfigFile(){
		if(config == null){
			File mcDir = (File) FMLInjectionData.data()[6];
			File configDir = null;
			configDir = new File(mcDir, "config");
			File configFile = new File(configDir, "ExtraObjects.cfg");
			config = new Configuration(configFile);
		}
		return config;
	}

	static public boolean debug(){
		boolean debug;
		Configuration cfg = getConfigFile();
		try {
			cfg.load();
			debug = cfg.getBoolean("debug mode", "mode", false, "debug");
		} finally {
			cfg.save();
		}
		return debug;
	}

	static public void writeData(String s){
		try {
			File data = new File((File) FMLInjectionData.data()[6], "data.txt");
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(data, true)));
			pw.println(s);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean quality() {
		boolean debug;
		Configuration cfg = getConfigFile();
		try {
			cfg.load();
			debug = cfg.getBoolean("draw_quality", "mode", true, "high draw quality (high: true)");
		} finally {
			cfg.save();
		}
		return debug;
	}
}
