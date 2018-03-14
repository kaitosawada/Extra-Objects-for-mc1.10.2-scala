package com.mito.exobj.lib.render.model;

import com.mito.exobj.module.exobject.Brace;
import net.minecraft.util.math.Vec3d;

public class BraceShapes implements IDrawBrace {

	public IDrawBrace[] planes;

	public BraceShapes(IDrawBrace... list) {
		planes = list;
	}

	@Override
	public IDrawable getModel(ILineBrace bc, double size, double roll, Vec3d pos) {
		BB_ModelGroup ret = new BB_ModelGroup();
		for (IDrawBrace plane : planes) {
			if (plane != null)
				ret.add(plane.getModel(bc, size, roll, pos));
		}
		return ret;
	}

}
