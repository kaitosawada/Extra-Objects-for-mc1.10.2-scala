package com.mito.exobj.lib;

import java.util.HashMap;
import java.util.Map;

import com.mito.exobj.lib.render.BB_Render;
import com.mito.exobj.lib.data.ChunkWorldManager;
import org.apache.logging.log4j.Level;

import com.mito.exobj.utilities.MyLogger;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class ExtraObjectRegistry {

	public static Map<String, Class> stringToClassMapping = new HashMap<String, Class>();
	public static Map<Class, String> classToStringMapping = new HashMap<Class, String>();
	public static Map<Class, BB_Render> classToRenderMapping = new HashMap<Class, BB_Render>();

	/**
	 * adds a mapping between FObj classes and both a string representation and an ID
	 */
	public static void addMapping(Class ioclass, String name, BB_Render render) {
		if (stringToClassMapping.containsKey(name)) {
			throw new IllegalArgumentException("ID is already registered: " + name);
		} else {
			stringToClassMapping.put(name, ioclass);
			classToStringMapping.put(ioclass, name);
			classToRenderMapping.put(ioclass, render);
		}
	}

	/**
	 * Create a new instance of an entity in the world by using the entity list.
	 */
	public static ExtraObject createExObjByName(String p_75620_0_, World p_75620_1_) {
		ExtraObject iobj = null;

		try {
			Class oclass = stringToClassMapping.get(p_75620_0_);

			if (oclass != null) {
				iobj = (ExtraObject) oclass.getConstructor(World.class).newInstance(new Object[]{p_75620_1_});
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return iobj;
	}

	/**
	 * create a new instance of an entity from NBT store
	 */

	public static ExtraObject createExObjFromNBT(NBTTagCompound nbt, World world) {
		return createExObjFromNBT(nbt, world, -1);
	}

	public static ExtraObject createExObjFromNBT(NBTTagCompound nbt, World world, int id) {
		ExtraObject exObj = null;

		Class oclass = null;
		try {
			if (nbt.getString("id") == null) {
				MyLogger.info("id is null");
			}
			oclass = stringToClassMapping.get(nbt.getString("id"));

			if (oclass == null) {
				MyLogger.info("class is null " + nbt.getString("id"));
			}
			if (oclass != null) {
				exObj = (ExtraObject) oclass.getConstructor(World.class).newInstance(new Object[]{world});
				if (id != -1) {
					exObj.BBID = id;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (exObj != null) {
			try {
				exObj.readFromNBT(nbt);
			} catch (Exception e) {
				FMLLog.log(Level.ERROR, e,
						"An Entity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
						nbt.getString("id"), oclass.getName());
				exObj = null;
			}
		} else {
			MyLogger.warn("Skipping Extra Object with id " + nbt.getString("id"));
		}

		return exObj;
	}

	/**
	 * Gets the string representation of a specific entity.
	 */
	public static String getBraceBaseString(ExtraObject p_75621_0_) {
		return classToStringMapping.get(p_75621_0_.getClass());
	}

	public static BB_Render getBraceBaseRender(ExtraObject p_75621_0_) {
		return classToRenderMapping.get(p_75621_0_.getClass());
	}

	public static ExtraObject syncBraceBaseFromNBT(NBTTagCompound nbt, World world, int id) {
		ExtraObject base = ChunkWorldManager.getWorldData(world).getBraceBaseByID(id);
		if (base != null) {
			base.readFromNBT(nbt);
			if (base.datachunk != null)
				base.datachunk.modified();
		} else {
			//PacketHandler.INSTANCE.sendToAll(new DeletePacketProcessor(id));
			MyLogger.warn("Skipping Entity with id " + nbt.getString("id"));
		}
		return base;
	}

}
