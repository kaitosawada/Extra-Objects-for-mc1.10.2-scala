package com.mito.exobj.lib.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.lib.render.model.IDrawable;

public class BB_OutputHandler {

	public static int groupNum = 0;

	public static void outputObj(String name, File dir, IDrawable model) throws IOException {
		if (model == null)
			return;
		File file = new File(dir, name + ".obj");
		if (file.exists()) {
			return;
		}
		file.createNewFile();
		File file1 = new File(dir, name + ".mtl");
		if (file1.exists()) {
			return;
		}
		file1.createNewFile();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		List<String> headers = new ArrayList<String>();
		List<String> vertexs = new ArrayList<String>();
		List<String> vertexs_t = new ArrayList<String>();
		List<String> vertexs_n = new ArrayList<String>();
		List<String> group = new ArrayList<String>();

		headers.add("# This file made by Extra Objects for Minecraft 1.7.10.");
		headers.add("");
		headers.add("mtllib " + name + ".mtl");
		headers.add("g default");

		groupNum = 0;
		model.writeObj(vertexs, vertexs_t, vertexs_n, group);
		if (vertexs.isEmpty()) {
			bw.close();
			return;
		}

		List<String> obj = new ArrayList<String>();
		obj.addAll(headers);
		obj.addAll(vertexs);
		obj.addAll(vertexs_t);
		obj.addAll(vertexs_n);
		obj.addAll(group);
		for (String s : obj) {
			bw.write(s + "\n", 0, s.length() + 1);
		}

		bw.close();

		List<String> mtl = new ArrayList<String>();
		mtl.add("newmtl default");
		mtl.add("illum 2");
		mtl.add("Kd 0.50 0.50 0.50");
		mtl.add("Ka 0.00 0.00 0.00");
		mtl.add("Tf 1.00 1.00 1.00");
		mtl.add("Ni 1.00");
		mtl.add("");
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1));
		for (String s : mtl) {
			bw1.write(s + "\n", 0, s.length() + 1);
		}
		bw1.close();
	}

}
