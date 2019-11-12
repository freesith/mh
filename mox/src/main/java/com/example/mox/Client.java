package com.example.mox;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class Client {

    final List<Interceptor> interceptors;

    public Client(Builder builder) {
        builder.addInterceptor(new MockInterceptor());
        interceptors = new ArrayList<>();
    }

    static class Builder {

        public Builder xxx() {
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {

            return this;
        }

//        private Client test() {
//
//            /**
//             * mv.visitTypeInsn(Opcodes.NEW, "com/example/mox/MockInterceptor");
//             *             mv.visitInsn(Opcodes.DUP);
//             *             mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/example/mox/MockInterceptor", "<init>", "()V", false);
//             *             mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/example/mox/Test", "addInterceptor", "(Lokhttp3/Interceptor;)V", false);
//             *
//             */
//            addInterceptor(new MockInterceptor());
//            return new OkHttpClient();
//        }
    }

}
