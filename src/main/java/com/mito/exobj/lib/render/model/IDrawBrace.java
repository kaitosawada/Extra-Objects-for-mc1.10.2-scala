package com.mito.exobj.lib.render.model;

import com.mito.exobj.module.exobject.Brace;
import net.minecraft.util.math.Vec3d;

public interface IDrawBrace {

	IDrawable getModel(ILineBrace bc , double size, double roll, Vec3d pos);

}
