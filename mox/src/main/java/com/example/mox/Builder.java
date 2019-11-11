package com.example.mox;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class Builder {

    private Builder addInterceptor(Interceptor interceptor) {

        return this;
    }

    private OkHttpClient test() {

        /**
         * mv.visitTypeInsn(Opcodes.NEW, "com/example/mox/MockInterceptor");
         *             mv.visitInsn(Opcodes.DUP);
         *             mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/example/mox/MockInterceptor", "<init>", "()V", false);
         *             mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/example/mox/Test", "addInterceptor", "(Lokhttp3/Interceptor;)V", false);
         *
         */
        addInterceptor(new MockInterceptor());
        return new OkHttpClient();
    }
}
