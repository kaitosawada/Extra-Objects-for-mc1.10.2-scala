package com.mito.exobj.lib.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class RenderClassAdapter extends ClassVisitor implements Opcodes {

	public static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.RenderGlobal";

	public RenderClassAdapter(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		String srgMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, name, desc);
		String srgDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc);
		//MyLogger.warn("method : " + list + "  desc : " + srgDesc);
		if (("renderEntities".equals(srgMethod) || "a".equals(srgMethod)) && "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V".equals(srgDesc)) {
			return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	public static class MethodAdapter extends MethodVisitor {
		public MethodAdapter(MethodVisitor mv) {
			super(ASM4, mv);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {
				super.visitVarInsn(ALOAD, 1);
				super.visitVarInsn(ALOAD, 2);
				super.visitVarInsn(FLOAD, 3);
				super.visitMethodInsn(INVOKESTATIC, "com/mito/exobj/lib/render/RenderHandler", "onRenderEntities", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V", false);
			}
			if (mv != null) {
				mv.visitInsn(opcode);
			}
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
		}
	}

}
