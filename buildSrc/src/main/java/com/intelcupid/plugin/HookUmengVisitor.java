package com.intelcupid.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class HookUmengVisitor extends ClassVisitor implements Opcodes {

    private String mClassName;

    public HookUmengVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("LifecycleClassVisitor : visitMethod : " + name);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if("okhttp3/OkHttpClient$Builder.class".equals(this.mClassName)) {
            if ("build".equals(name) ) {
                return new FieldManagerHooker(mv);
            }
        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}