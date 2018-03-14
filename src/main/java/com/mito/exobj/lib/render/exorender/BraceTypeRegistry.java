package com.mito.exobj.lib.render.exorender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mito.exobj.lib.render.CubeJoint;
import com.mito.exobj.lib.data.BB_LoadModel;
import com.mito.exobj.lib.render.model.BB_Model;
import com.mito.exobj.lib.render.model.BB_Polygon;
import com.mito.exobj.lib.render.model.D_Ellipse;
import com.mito.exobj.lib.render.model.IDrawBrace;
import com.mito.exobj.lib.render.model.Pattern;
import com.mito.exobj.lib.render.model.Vertex;
import com.mito.exobj.utilities.MyLogger;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;

public class BraceTypeRegistry {

	public static Map<String, IDrawBrace> stringToFigureMapping = new HashMap<String, IDrawBrace>();
	public static Map<IDrawBrace, String> figureToStringMapping = new HashMap<IDrawBrace, String>();
	public static Map<String, IJoint> stringToJointMapping = new HashMap<String, IJoint>();
	public static Map<IJoint, String> jointToStringMapping = new HashMap<IJoint, String>();
	public static List<String> shapeList = new ArrayList<String>();
	public static List<String> patternList = new ArrayList<String>();
	public static List<String> typeList = new ArrayList<String>();

	public static void addMapping(IDrawBrace figure, String name) {
		addMapping(figure, name, true);
	}

	public static void addMapping(IDrawBrace figure, String name, boolean init) {
		if (stringToFigureMapping.containsKey(name)) {
			MyLogger.warn("ID is already registered: " + name);
		} else {
			stringToFigureMapping.put(name, figure);
			figureToStringMapping.put(figure, name);
			typeList.add(name);
			if (init) {
				if (figure instanceof Pattern) {
					patternList.add(name);
				} else {
					shapeList.add(name);
				}
			}
		}
	}

	public static void addMapping(IJoint figure, String name) {
		stringToJointMapping.put(name, figure);
		jointToStringMapping.put(figure, name);
	}

	public static IDrawBrace getFigure(String name) {
		if (!stringToFigureMapping.containsKey(name)) {
			return stringToFigureMapping.get("square");
		}
		return stringToFigureMapping.get(name);
	}

	public static void loadModels() {
		addMapping(new CubeJoint(), "CubeJoint");
		//addMapping(null, "");
		addMapping(createSquare(1, 1, 0, 0), "square", true);
		/*addMapping(createElipse(1, 1, 0, 0), "round", true);
		addMapping(createSquare(0.2, 1, 0, 0), "vertical", true);
		addMapping(createSquare(1, 0.2, 0, 0), "horizontal", true);*/
		//addMapping(create2d(0.5, 0.55, -0.5, 0.55, -0.5, 0.45, -0.05, 0.45, -0.05, -0.45, -0.5, -0.45, -0.5, -0.55, +0.5, -0.55, 0.5, -0.45, 0.05, -0.45, 0.05, 0.45, 0.5, 0.45), "H-section", true);
		//addMapping(new BraceShapes(createSquare(0.1, 1.0, -0.45, 0), createSquare(1, 0.1, 0, -0.45)), "Equal-Angle", true);
		//addMapping(new Pattern(0.6, createRectangle(0, 0, 0.15, 0.9, 0.9, 0.3), createRectangle(0, 0, 0.3, 0.3, 0.3, 0.6)), "pattern", true);
		//addMapping(create2d(-0.1123, 0.1545, -0.4756, 0.1545, -0.1817, -0.0590, -0.2939, -0.4045, 0.0000, -0.1910, 0.2939, -0.4045, 0.1817, -0.0590, 0.4756, 0.1545, 0.1123, 0.1545, 0.0000, 0.5000), "star", true);
		if (Loader.isModLoaded("NGTLib")) {
			//addMapping(new Pattern(0.6, new NGTOWrapper("chino.ngto")), "ngto");
		}
		BB_LoadModel.load();
	}

	public static String getName(IDrawBrace shape) {
		return figureToStringMapping.get(shape);
	}

	static public BB_Polygon createSquare(double width, double height, double x, double y) {
		BB_Polygon ret = new BB_Polygon();
		ret.getLine().add(new Vertex(x + width / 2, y + height / 2, 0, x + width / 2, y - height / 2));
		ret.getLine().add(new Vertex(x - width / 2, y + height / 2, 0, x - width / 2, y - height / 2));
		ret.getLine().add(new Vertex(x - width / 2, y - height / 2, 0, x - width / 2, y + height / 2));
		ret.getLine().add(new Vertex(x + width / 2, y - height / 2, 0, x + width / 2, y + height / 2));
		return ret;
	}

	static public BB_Polygon create2d(double... array) {
		if (array.length % 2 == 1) {
			return null;
		}
		BB_Polygon ret = new BB_Polygon();
		for (int n = 0; n < array.length / 2; n++) {
			Vertex v = new Vertex(array[2 * n], array[2 * n + 1]);
			ret.getLine().add(v);
		}
		return ret;
	}

	public static BB_Model createRectangle(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
		double maxX = x + sizeX / 2;
		double maxY = y + sizeY / 2;
		double maxZ = z + sizeZ / 2;
		double minX = x - sizeX / 2;
		double minY = y - sizeY / 2;
		double minZ = z - sizeZ / 2;
		Vec3d n1 = new Vec3d(0, 0, 1);
		Vec3d n2 = new Vec3d(0, 0, -1);
		Vec3d n3 = new Vec3d(0, 1, 0);
		Vec3d n4 = new Vec3d(0, -1, 0);
		Vec3d n5 = new Vec3d(1, 0, 0);
		Vec3d n6 = new Vec3d(-1, 0, 0);
		return new BB_Model(
				new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxX, maxY, n1), new Vertex(minX, maxY, maxZ, minX, maxY, n1), new Vertex(minX, minY, maxZ, minX, minY, n1), new Vertex(maxX, minY, maxZ, maxX, minY, n1)),
				new BB_Polygon(new Vertex(maxX, maxY, minZ, minX, maxY, n2), new Vertex(maxX, minY, minZ, minX, minY, n2), new Vertex(minX, minY, minZ, maxX, minY, n2), new Vertex(minX, maxY, minZ, maxX, maxY, n2)),
				new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxZ, maxX, n3), new Vertex(maxX, maxY, minZ, minZ, maxX, n3), new Vertex(minX, maxY, minZ, minZ, minX, n3), new Vertex(minX, maxY, maxZ, maxZ, minX, n3)),
				new BB_Polygon(new Vertex(maxX, minY, maxZ, minZ, maxX, n4), new Vertex(minX, minY, maxZ, minZ, minX, n4), new Vertex(minX, minY, minZ, maxZ, minX, n4), new Vertex(maxX, minY, minZ, maxZ, maxX, n4)),
				new BB_Polygon(new Vertex(maxX, maxY, maxZ, maxY, maxZ, n5), new Vertex(maxX, minY, maxZ, minY, maxZ, n5), new Vertex(maxX, minY, minZ, minY, minZ, n5), new Vertex(maxX, maxY, minZ, maxY, minZ, n5)),
				new BB_Polygon(new Vertex(minX, maxY, maxZ, minY, maxZ, n6), new Vertex(minX, maxY, minZ, minY, minZ, n6), new Vertex(minX, minY, minZ, maxY, minZ, n6), new Vertex(minX, minY, maxZ, maxY, maxZ, n6)));
	}

	public static BB_Model createRectangle(Vec3d v, double d) {
		return BraceTypeRegistry.createRectangle(v.xCoord, v.yCoord, v.zCoord, d, d, d);
	}

	public static D_Ellipse createElipse(double i, double j, double k, double l) {
		return new D_Ellipse(new Vec3d(k, l, 0), new Vec3d(0, 0, 1), i, j);
	}

	public static IJoint getJoint(String name) {
		if (!stringToJointMapping.containsKey(name)) {
			return null;
		}
		return stringToJointMapping.get(name);
	}

	public static String getJointName(IJoint joint) {
		if (joint == null) {
			return "";
		}
		return jointToStringMapping.get(joint);
	}

}
