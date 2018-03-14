package com.mito.exobj.lib.render.model;

import net.minecraft.util.math.Vec3d;

public interface ITransform {

	void transform(Mat4 m);

	default void rotate(Vec3d cent, double rol, double pit, double yaw) {
		transform(Mat4.createVec3(cent).rpyRotation(rol, pit, yaw).translate(cent.scale(-1)));
	}

	default void scale(Vec3d cent, double i) {
		transform(Mat4.createVec3(cent).scale(i).translate(cent.scale(-1)));
	}

	default void translate(Vec3d v){
		transform(Mat4.createVec3(v));
	}
}
