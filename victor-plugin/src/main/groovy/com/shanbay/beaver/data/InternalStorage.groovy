package com.shanbay.beaver.data

import org.gradle.api.Project

/**
 * Created by chan on 2017/6/1.
 */
public class InternalStorage {

    private static File sMainDir;

    public static void init(Project project) {
        sMainDir = new File(project.buildDir, "beaver")
        if (!sMainDir.exists()) {
            sMainDir.mkdir()
        }
    }

    public static File getInternalStorageDir() {
        return sMainDir
    }
}
