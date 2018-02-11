# what is transform

从1.5开始，gradle插件包含了一个叫Transform的API，这个API允许第三方插件在class文件转为为dex文件前操作编译好的class文件，这个API的目标就是简化class文件的自定义的操作而不用对Task进行处理，并且可以更加灵活地进行操作。

# Transform 各个方法的含义

```java
class TransformImpl extends Transform {
    Project project
    public TransformTest(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "Beaver"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }


    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }


    @Override
    boolean isIncremental() {
        return false;
    }


    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,  TransformOutputProvider outputProvider, boolean isIncremental) throws     IOException, TransformException, InterruptedException {
    }
}
```

- getName() 是transform的名称

- getInputTypes() 指明transform的输入类型 一般有class, resource这几种

- getScopes() 指明transform的作用范围 一般有PROJECT， SUB_PROJECTS等等

- isIncremental() 是否是增量的

- transform(...) 具体处理业务的地方


比如Proguard这个Transform有两种输入文件，一种是class文件（含jar），另一种是资源文件，这个Task是做混淆用的，class文件就是ProGuardTransform依赖的上一个Transform的输出产物，而资源文件可以是混淆时使用的配置文件。

因此根据上面的规则，这个Transform最终在控制台显示的名字就是

> transformClassesAndResourcesWithProguardForDebug

# 注册transform

```groovy
def android = project.extensions.getByType(AppExtension)
def transform = new TransformImpl(project)
android.registerTransform(transform)
```

# with asm

```java
@Override
void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        /**
        * 遍历输入文件
        */
        inputs.each { TransformInput input ->
        /**
        * 遍历目录
        */
        input.directoryInputs.each { DirectoryInput directoryInput ->
        /** 
        * 获得产物的目录
        */
        File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
        String buildTypes = directoryInput.file.name
        String productFlavors = directoryInput.file.parentFile.name
        //这里进行我们的处理 TODO
        project.logger.error "Copying ${directoryInput.name} to ${dest.absolutePath}"
        /**
        * 处理完后拷到目标文件
        */
        FileUtils.copyDirectory(directoryInput.file, dest);
    }

    /**
    * 遍历jar
    */
    input.jarInputs.each { 
        JarInput jarInput -> 
            String destName = jarInput.name;
            /**
            * 重名名输出文件,因为可能同名,会覆盖
            */
            def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath);
            if (destName.endsWith(".jar")) {
                destName = destName.substring(0, destName.length() - 4);
            }
            /**
            * 获得输出文件
            */
            File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);

            //处理jar进行字节码注入处理TODO

            FileUtils.copyFile(jarInput.file, dest);
            project.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"
    }
}
```

# 扩展参数

常见的比如
android {
    compileSdkVersion ....
}

它的实现其实是首先声明一个类
```groovy
public class PluginExtension {
    HashSet<String> includePackage = []
    HashSet<String> excludeClass = []
}
```

之后便是注册

```groovy
project.extensions.create("hotpatch", PluginExtension)
```

使用的话：

```groovy
hotpatch {
    includePackage = ["xxx"]
    excludeClass = ["xxx"]
}
```
