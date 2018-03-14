package com.mito.exobj.lib.render.exorender;

import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.lib.render.model.IDrawable;
import net.minecraft.util.math.Vec3d;

public interface IJoint {
	IDrawable getModel(Brace brace, Vec3d pos);

}
