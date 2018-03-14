package com.mito.exobj.lib.render;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.lib.ExtraObject;
import net.minecraft.util.math.Vec3d;

public class VBOList {

	public List<VBOHandler> array = new ArrayList<VBOHandler>();
	public Vec3d v = new Vec3d(0, 0, 0);

	public VBOList(VBOHandler... a) {
		for (int n = 0; n < a.length; n++) {
			this.array.add(a[n]);
		}
	}

	public void draw() {
		for (int n = 0; n < array.size(); n++) {
			array.get(n).draw();
		}
	}

	public void draw(int glmode) {
		for (int n = 0; n < array.size(); n++) {
			array.get(n).draw(glmode);
		}
	}

	public void delete() {
		for (int n = 0; n < array.size(); n++) {
			array.get(n).delete();
		}
		array = new ArrayList<VBOHandler>();
	}

	public void add(VBOHandler vbo) {
		this.array.add(vbo);
	}

	public void updateBrightness(ExtraObject base, float f) {
		for (int n = 0; n < array.size(); n++) {
			array.get(n).updateBrightness(base, f);
		}
	}

}
