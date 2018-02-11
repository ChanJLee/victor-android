package com.shanbay.beaver.tramsform;

import com.android.build.api.transform.*;
import com.android.build.gradle.internal.pipeline.TransformManager
import com.shanbay.beaver.asm.BeaverClassVisitor
import com.shanbay.beaver.data.ClassIdentifier
import com.shanbay.beaver.data.HookUnit
import com.shanbay.beaver.data.InternalStorage
import com.shanbay.beaver.data.HookEvent
import com.shanbay.beaver.data.ProjectHelper
import com.shanbay.beaver.log.Logger
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by chan on 2017/6/1.
 */
public class ShanbayInjectTransform extends Transform {

    @Override
    public String getName() {
        return "beaver";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        HashSet<HookUnit> units = ProjectHelper.hookUnits
        List<HookUnit> hookUnits = new ArrayList<>()
        units.each {
            HookUnit unit ->
                hookUnits.add(unit)
        }

        inputs.each {
            TransformInput transformInput ->
                transformInput.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        File destDir = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                        File srcDir = directoryInput.file

                        if (srcDir) {
                            srcDir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                                File classFile ->

                                    String classPath = classFile.absolutePath.replace(srcDir.absolutePath + File.separator, "")
                                    String className = classPath.replace(File.separator, ".").replace(".class", "")

                                    byte[] src = IOUtils.toByteArray(new FileInputStream(classFile))
                                    File destFile = new File(destDir.absolutePath + File.separator + classPath)
                                    List<File> destFiles = new ArrayList<>()
                                    destFiles.add(destFile)
                                    if (ProjectHelper.beaver.outputModifiedClass) {
                                        destFiles.add(new File(InternalStorage.internalStorageDir.absolutePath + File.separator + classPath))
                                    }

                                    for (ClassIdentifier classIdentifier : ProjectHelper.getIncludeClassIdentifier()) {
                                        if (classIdentifier.match(className)) {
                                            Logger.logInfo("hook class: ${className}")
                                            ClassReader classReader = new ClassReader(src)
                                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
                                            ClassVisitor classVisitor = new BeaverClassVisitor(hookUnits, classWriter)
                                            classReader.accept(classVisitor, 0)
                                            byte[] clazz = classWriter.toByteArray()
                                            writeClass(clazz, destFiles)
                                            return
                                        }
                                    }

                                    writeClass(src, destFiles)
                            }
                        }
                }
        }

        Set<String> jarSet = new HashSet<>()
        inputs.each {
            TransformInput input ->
                input.jarInputs.each {
                    JarInput jarInput ->
                        String destName = jarInput.file.name;
                        if (jarSet.contains(jarInput.file.absolutePath)) {
                            return
                        }

                        jarSet.add(jarInput.file.absolutePath)
                        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8);
                        if (destName.endsWith(".jar")) {
                            destName = destName.substring(0, destName.length() - 4);
                        }

                        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);
                        if (dest.exists()) {
                            return
                        }

                        File destWithPackage = dest.getParentFile()
                        if (!destWithPackage.exists()) {
                            destWithPackage.mkdirs()
                        }

                        if (!dest.exists()) {
                            dest.createNewFile()
                        }

                        def file = new JarFile(jarInput.file)
                        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(dest))
                        Enumeration enumeration = file.entries()

                        while (enumeration.hasMoreElements()) {
                            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                            InputStream inputStream = file.getInputStream(jarEntry)
                            String entryName = jarEntry.getName();
                            ZipEntry zipEntry = new ZipEntry(entryName);
                            jarOutputStream.putNextEntry(zipEntry);

                            byte[] src = IOUtils.toByteArray(inputStream)
                            if (entryName.endsWith(".class")) {
                                String className = entryName.replace(File.separator, ".").replace(".class", "")
                                boolean matched = false
                                for (ClassIdentifier classIdentifier : ProjectHelper.getIncludeClassIdentifier()) {
                                    if (classIdentifier.match(className)) {
                                        matched = true
                                        ClassReader classReader = new ClassReader(src)
                                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
                                        ClassVisitor classVisitor = new BeaverClassVisitor(hookUnits, classWriter)
                                        classReader.accept(classVisitor, 0)
                                        byte[] clazz = classWriter.toByteArray()
                                        jarOutputStream.write(clazz)
                                        Logger.logInfo("hook class: ${className}")
                                        break
                                    }
                                }

                                if (!matched) {
                                    jarOutputStream.write(src)
                                }
                            } else {
                                jarOutputStream.write(src)
                            }
                            jarOutputStream.closeEntry()
                        }
                }
        }
    }

    private static void writeClass(byte[] content, List<File> destFiles) {
        destFiles.each {
            File destFile ->
                File destWithPackage = destFile.getParentFile()
                if (!destWithPackage.exists()) {
                    destWithPackage.mkdirs()
                }

                if (!destFile.exists()) {
                    destFile.createNewFile()
                }

                IOUtils.write(content, new FileOutputStream(destFile))
        }
    }

}
