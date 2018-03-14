package com.mito.exobj.lib.asm;

import com.mito.exobj.utilities.MyLogger;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChunkClassAdapter extends ClassVisitor implements Opcodes {

	public static final String TARGET_CLASS_NAME = "net.minecraft.world.chunk.Chunk";

	public ChunkClassAdapter(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		String srgMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, name, desc);
		String srgDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc);
		//MyLogger.warn("method : " + srgMethod + "  desc : " + srgDesc);
		if (("getLightFor".equals(srgMethod) || "a".equals(srgMethod)) && "(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I".equals(srgDesc)) {
			return new LightValueMethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	public static class LightValueMethodAdapter extends MethodVisitor {
		public LightValueMethodAdapter(MethodVisitor mv) {
			super(ASM4, mv);
		}

		@Override
		public void visitInsn(int opcode) {
			if(opcode == IRETURN){
				//BraceCoreHooks.getLightFor(value, this, skyblock, pos);
				super.visitVarInsn(ALOAD, 0);
				super.visitVarInsn(ALOAD, 1);
				super.visitVarInsn(ALOAD, 2);
				super.visitMethodInsn(INVOKESTATIC, "com/mito/exobj/lib/asm/BraceCoreHooks", "getLightValue", "(ILnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I", false);
			}
			if (mv != null) {
				mv.visitInsn(opcode);
			}
		}
	}

}
