package com.mito.exobj.lib.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Transformer implements IClassTransformer, Opcodes {

	private static final Map<String, Function<ClassWriter, ClassVisitor>> map = new HashMap<String, Function<ClassWriter, ClassVisitor>>() {
		{
			put(WorldClassAdapter.TARGET_CLASS_NAME, cw -> new WorldClassAdapter(ASM4, cw));
			put(RenderClassAdapter.TARGET_CLASS_NAME, cw -> new RenderClassAdapter(ASM4, cw));
			put(MouseOverClassAdapter.TARGET_CLASS_NAME, cw -> new MouseOverClassAdapter(ASM4, cw));
			put(ChunkUpdateClassAdapter.TARGET_CLASS_NAME, cw -> new ChunkUpdateClassAdapter(ASM4, cw));
			//ExtraObjectのライトとか作るつもり
			//put(ChunkClassAdapter.TARGET_CLASS_NAME, cw -> new ChunkClassAdapter(ASM4, cw));
		}
	};

	@Override
	public byte[] transform(String name, String transformedName, byte[] data) {
		if (map.containsKey(transformedName)) {
			try {
				ClassReader cr = new ClassReader(data);
				ClassWriter cw = new ClassWriter(1);
				ClassVisitor cv = map.get(transformedName).apply(cw);
				cr.accept(cv, 0);
				return cw.toByteArray();
			} catch (Exception e) {
				throw new RuntimeException("failed : BraceCollisionTransformer loading", e);
			}
		}
		return data;
	}
}
