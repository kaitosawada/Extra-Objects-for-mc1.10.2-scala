package com.mito.exobj.lib.render;


import com.mito.exobj.lib.render.exorender.BraceTypeRegistry;
import com.mito.exobj.lib.render.exorender.IJoint;
import com.mito.exobj.lib.render.model.BB_ModelGroup;
import com.mito.exobj.lib.render.model.IDrawable;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.utilities.MitoMath;
import net.minecraft.util.math.Vec3d;

public class CubeJoint implements IJoint {

	@Override
	public IDrawable getModel(Brace brace, Vec3d pos) {
		BB_ModelGroup ret = new BB_ModelGroup();
		ret.add(BraceTypeRegistry.createRectangle(MitoMath.sub_vector(brace.line.getStart(), pos), brace.size * 1.414));
		ret.add(BraceTypeRegistry.createRectangle(MitoMath.sub_vector(brace.line.getEnd(), pos), brace.size * 1.414));
		return ret;
	}
}
