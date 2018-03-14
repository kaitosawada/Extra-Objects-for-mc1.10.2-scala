package com.mito.exobj.lib.render.model;

import com.mito.exobj.module.exobject.Brace;
import net.minecraft.util.math.Vec3d;

public class Pattern implements IDrawBrace {

	public IDrawable model;
	public double length;

	public Pattern(double length, IDrawable model) {
		this.model = model;
		this.length = length;
	}

	@Override
	public BB_Model getModel(ILineBrace bc , double size, double roll, Vec3d pos) {
		return null;
	}

}
