import com.android.build.gradle.AppExtension
import com.intelcupid.plugin.MoxyTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class MoxyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

//        project.configurations.all { configuration ->
//            def name = configuration.name
//            System.out.println("this configuration is ${name}")
//            if (name != "implementation" && name != "compile") {
//                return
//            }
//            //为Project加入Gson依赖
////            configuration.dependencies.add(project.dependencies.create("com.google.code.gson:gson:2.8.2"))
//        }

        project.extensions.add("moxConfig", MoxyExtension)
        def android = project.extensions.getByType(AppExtension)
        def moxConfig = project.extensions.getByType(MoxyExtension)
//        android.registerTransform(new OkHttpTransform())

        println(project.getBuildFile().absolutePath)
        def moxTask = project.tasks.create("mox", MoxyTask.class, new Action<MoxyTask>() {
            @Override
            void execute(MoxyTask task) {
                task.assetPath = "111"
            }
        })
    }
}