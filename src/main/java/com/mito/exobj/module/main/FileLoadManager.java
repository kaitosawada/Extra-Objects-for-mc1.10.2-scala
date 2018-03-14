package com.mito.exobj.module.main;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class FileLoadManager {

	static public File modelDir;
	static public File objDir;
	static public File shapesDir;
	static public File source;

	static public void resisterFiles(FMLPreInitializationEvent event) {
		File mcDir = (File) FMLInjectionData.data()[6];
		source = event.getSourceFile();
		try {
			File modDir = new File(mcDir, "mods");
			modelDir = new File(modDir, "brace");
			modelDir.mkdir();
			shapesDir = new File(modelDir, "shapes");
			objDir = new File(modelDir, "obj");
			objDir.mkdir();
			if (shapesDir.listFiles() == null || shapesDir.listFiles().length < 2) {
				shapesDir.mkdir();
				//File sd2 = new File(sourceDir, "/assets/exobj/jsons");
				if (!ConfigManager.debug()) {
					try {
						@SuppressWarnings("resource")
						JarFile jar = new JarFile(source.getAbsolutePath());
						@SuppressWarnings("resource")
						JarInputStream jarInStream = new JarInputStream(new BufferedInputStream(new FileInputStream(source)));
						while (true) {
							JarEntry entry = jarInStream.getNextJarEntry();
							if (entry == null)
								break;
							if (entry.isDirectory()) {
							} else {
								String name = entry.getName();
								if (getSuffix(name).equals("json") && getDirectoryName(name).equals("jsons")) {

									final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
									while (true) {
										int iRead = jarInStream.read();
										if (iRead < 0)
											break;
										outStream.write(iRead);
									}
									outStream.flush();
									outStream.close();
									String s = new String(outStream.toByteArray());
									//System.out.println("file " + list + " " + s);

									File file = new File(shapesDir, getFileName(name));
									file.createNewFile();
									FileWriter filewriter = new FileWriter(file);
									filewriter.write(s);
									filewriter.close();
								}
							}
							jarInStream.closeEntry();

							/*
							if (file != null) {
								String list = file.getName();
								File file2 = new File(shapesDir, list);
								try {
									file.renameTo(file2);
								} catch (SecurityException e) {
									System.out.println(e);
								} catch (NullPointerException e) {
									System.out.println(e);
								}
							}
							*/
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			/*GroupsDir = new File(modelDir, "groups");
			GroupsDir.mkdir();
			ObjsDir = new File(modelDir, "objects");
			ObjsDir.mkdir();*/
		} finally

		{
		}
	}


	public static String getSuffix(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1) {
			return name.substring(point + 1);
		}
		return name;
	}

	public static String getFileName(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf("/");
		if (point != -1) {
			return name.substring(point + 1);
		}
		return name;
	}

	public static String getDirectoryName(String name) {
		if (name == null)
			return null;
		String[] a = name.split("/");
		if (a.length > 1)
			return a[a.length - 2];
		return null;
	}
}
