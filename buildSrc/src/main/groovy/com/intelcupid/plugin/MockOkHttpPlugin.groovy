import com.android.annotations.NonNull
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import com.intelcupid.plugin.HookUmengVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MockOkHttpPlugin extends Transform implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "MockOkHttpPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(@NonNull TransformInvocation transformInvocation) {
        println '--------------- hook umeng visit start --------------- '
        def startTime = System.currentTimeMillis()
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        if (transformInvocation.isIncremental()) {
            //遍历inputs
            inputs.each { TransformInput input ->
                //遍历jarInputs
                input.jarInputs.each { JarInput jarInput ->
                    if (jarInput.status != Status.NOTCHANGED) {
                        if (jarInput.status == Status.REMOVED) {
                            def jarName = jarInput.name
                            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                            if (jarName.endsWith(".jar")) {
                                jarName = jarName.substring(0, jarName.length() - 4)
                            }
                            def jarOutFile = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                            jarOutFile.delete()
                        } else {
                            handleJarInputs(jarInput, outputProvider)
                        }
                    }
                    handleJarInputs(jarInput, outputProvider)
                }
            }
        } else {

            if (outputProvider != null) {
                outputProvider.deleteAll()
            }
            //遍历inputs
            inputs.each { TransformInput input ->
                //遍历jarInputs
                input.jarInputs.each { JarInput jarInput ->
                    handleJarInputs(jarInput, outputProvider)
                }
            }
        }
        def cost = (System.currentTimeMillis() - startTime) / 1000
        println '--------------- hook umeng visit end --------------- '
        println "hook umeng cost ： $cost s"
    }

    /**
     * 处理Jar中的class文件
     */
    static void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (checkClassFile(entryName)) {
                    //class文件处理
                    println '----------- deal with "jar" class file <' + entryName + '> -----------'
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new HookUmengVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    /**
     * 检查class文件是否需要处理
     * @param fileName
     * @return
     */
    static boolean checkClassFile(String name) {
        //只处理需要的class文件
        return '''okhttp3/OkHttpClient$Builder.class''' == name
    }

}