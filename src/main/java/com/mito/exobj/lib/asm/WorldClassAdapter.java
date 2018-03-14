package com.mito.exobj.lib.asm;

import com.mito.exobj.utilities.MyLogger;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WorldClassAdapter extends ClassVisitor implements Opcodes {

	public static final String TARGET_CLASS_NAME = "net.minecraft.world.World";

	public WorldClassAdapter(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		String srgMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, name, desc);
		String srgDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc);
		//MyLogger.warn("method : " + srgMethod + "  desc : " + srgDesc);
		if (("getCollisionBoxes".equals(srgMethod) || "a".equals(srgMethod)) && "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;".equals(srgDesc)) {
			return new CollisionMethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	public static class CollisionMethodAdapter extends MethodVisitor {
		public CollisionMethodAdapter(MethodVisitor mv) {
			super(ASM4, mv);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == ARETURN) {
				super.visitVarInsn(ALOAD, 0);
				super.visitVarInsn(ALOAD, 2);
				super.visitVarInsn(ALOAD, 3);
				super.visitVarInsn(ALOAD, 1);
				super.visitMethodInsn(INVOKESTATIC, "com/mito/exobj/lib/asm/BraceCoreHooks", "getCollisionHook", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V", false);
			}
			if (mv != null) {
				mv.visitInsn(opcode);
			}
		}
	}

}
