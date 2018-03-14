package com.mito.exobj.lib.data;

import com.google.gson.stream.JsonReader;
import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import com.mito.exobj.lib.render.model.*;
import com.mito.exobj.module.main.FileLoadManager;
import com.mito.exobj.module.main.Main;
import com.mito.exobj.utilities.MyLogger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BB_LoadModel {

	public static void load() {
		File sd = FileLoadManager.shapesDir;
		List<File> jsons = new ArrayList<File>();
		loadJson(sd, jsons);
		try {
			for (int n = 0; n < jsons.size(); n++) {
				loadShapes(new InputStreamReader(new FileInputStream(jsons.get(n)), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//http://pastebin.com/raw/7k746Ten
	}

	public static void loadPastebin(String str) {
		String url_str = "http://pastebin.com/raw/" + str;

		URL url;
		URLConnection conn;
		try {
			url = new URL(url_str);
			conn = url.openConnection();/*
										String charset = Arrays.asList(conn.getContentType().split(";")).get(1);
										String encoding = Arrays.asList(charset.split("=")).get(1);*/
			loadShapes(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void loadUrl(JsonReader reader) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		reader.beginArray();
		while (reader.hasNext()) {
			String name = reader.nextString();
			list.add(name);
		}
		reader.endArray();
		for (int n = 0; n < list.size(); n++) {
			list.add(list.get(n));
		}
	}

	public static void loadJson(File file, List<File> list) {
		if (file != null && file.isDirectory()) {
			File[] files = file.listFiles();
			for (int n = 0; n < files.length; n++) {
				if (files[n] != null && files[n].canRead() && files[n].isFile() && files[n].getPath().endsWith(".json")) {
					list.add(files[n]);
				} else if (files[n].isDirectory()) {
					loadJson(files[n], list);
				}
			}
		}
	}

	public static void loadShapes(InputStreamReader isr) {
		String typename = null;
		String author = null;
		String load_type = null;
		try {
			JsonReader reader = new JsonReader(isr);
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("name")) {
					typename = reader.nextString();
					MyLogger.info("load " + typename);
				} else if (name.equals("author")) {
					author = reader.nextString();
				} else if (name.equals("load_type")) {
					load_type = reader.nextString();
				} else if (name.equals("pastebin")) {
					loadUrl(reader);
				} else if (name.equals("class")) {
					if (load_type != null && typename != null) {
						IDrawBrace add = loadClass(reader);
						if (add != null) {
							if (author == null || author.equals("")) {
								BraceTypeRegistry.addMapping(add, typename);
							} else {
								BraceTypeRegistry.addMapping(add, author + ":" + typename);
							}
						}
					} else {
						reader.skipValue();
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IDrawBrace loadClass(JsonReader reader) throws IOException {
		reader.beginObject();
		IDrawBrace ret = null;
		while (reader.hasNext()) {
			String name1 = reader.nextName();
			if (name1.equals("class_name")) {
				String classname = reader.nextString();
				if (classname.equals("D_Face")) {
					ret = loadFace(reader);
				} else if (classname.equals("D_Ellipse")) {
					ret = loadEllipse(reader);
				} else if (classname.equals("BraceShapes")) {
					ret = loadBraceShapes(reader);
				}
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return ret;
	}

	private static IDrawBrace loadBraceShapes(JsonReader reader) throws IOException {
		IDrawBrace[] shapes = null;
		double size = 1.0;
		while (reader.hasNext()) {
			String name1 = reader.nextName();
			if (name1.equals("shape_number")) {
				shapes = new IDrawBrace[reader.nextInt()];
			} else if (name1.equals("size_ratio")) {
				size = reader.nextDouble();
			} else if (name1.equals("shapes")) {
				int n = 0;
				reader.beginArray();
				while (reader.hasNext()) {
					shapes[n] = loadClass(reader);
					n++;
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}

		return new BraceShapes(shapes);
	}

	private static D_Ellipse loadEllipse(JsonReader reader) throws IOException {
		Vertex[] vertexs = null;
		double size = 1.0;
		D_Ellipse ret = null;
		while (reader.hasNext()) {
			String name1 = reader.nextName();
			if (name1.equals("size_ratio")) {
				size = reader.nextDouble();
			} else if (name1.equals("arguments")) {
				reader.beginArray();
				double[] a = new double[4];
				int n = 0;
				while (reader.hasNext()) {
					if (n < 4) {
						a[n] = reader.nextDouble();
					}
					n++;
				}
				reader.endArray();
				ret = BraceTypeRegistry.createElipse(a[0] * size, a[1] * size, a[2], a[3]);
			} else {
				reader.skipValue();
			}
		}
		return ret;
	}

	public static BB_Polygon loadFace(JsonReader reader) throws IOException {

		Vertex[] vertexs = null;
		double size = 1.0;
		while (reader.hasNext()) {
			String name1 = reader.nextName();
			if (name1.equals("vertex_number")) {
				vertexs = new Vertex[reader.nextInt()];
			} else if (name1.equals("size_ratio")) {
				size = reader.nextDouble();
			} else if (name1.equals("vertexs")) {
				{
					int n = 0;
					reader.beginArray();
					while (reader.hasNext()) {
						double[] da = new double[5];
						int n1 = 0;
						reader.beginArray();
						while (reader.hasNext()) {
							da[n1] = reader.nextDouble();
							n1++;
						}
						reader.endArray();
						if (n1 == 1) {
							vertexs[n] = new Vertex(da[0] * size, da[1] * size);
						} else if (n1 == 2) {
							vertexs[n] = new Vertex(da[0] * size, da[1] * size, da[2] * size);
						} else if (n1 == 4) {
							vertexs[n] = new Vertex(da[0] * size, da[1] * size, da[2] * size, da[3] * size, da[4] * size);
						} else {
							return null;
						}
						n++;
					}
					reader.endArray();
				}
			} else {
				reader.skipValue();
			}
		}
		if (vertexs == null || vertexs.length <= 2) {
			return null;
		}

		return new BB_Polygon(vertexs);
	}

}
