package com.intelcupid.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FieldManagerHooker extends MethodVisitor {


    public FieldManagerHooker(MethodVisitor mv) {
        super(Opcodes.ASM4, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.NEW, "com/example/mox/MockInterceptor");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/example/mox/MockInterceptor", "<init>", "()V", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "okhttp3/OkHttpClient$Builder", "addInterceptor", "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient$Builder;", false);
        mv.visitInsn(Opcodes.POP);
    }
}
