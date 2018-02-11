package com.shanbay.beaver.log

import com.shanbay.beaver.data.ProjectHelper
import org.gradle.api.Project

/**
 * Created by chan on 2017/6/2.
 */
public class Logger {

    private static Project sProject;

    public static void init(Project project) {
        sProject = project;
    }

    public static void logInfo(String message) {
        if (ProjectHelper.getBeaver().enableLog) {
            println("shanbay beaver: ${message}")
        }
    }

    public static void logWarn(String message) {
        if (ProjectHelper.getBeaver().enableLog) {
            println("shanbay beaver: ${message}")
        }
    }
}
