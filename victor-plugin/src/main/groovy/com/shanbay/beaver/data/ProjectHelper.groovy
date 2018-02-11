package com.shanbay.beaver.data

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.shanbay.beaver.log.Logger
import org.apache.commons.lang3.StringUtils
import org.gradle.api.Project;

/**
 * Created by chan on 2017/6/1.
 */
public class ProjectHelper {
    private static Project sProject;
    private static List<ClassIdentifier> sIncludeIdentifiers;
    private static String REGEX_PREFIX = "r:";

    public static void init(Project project) {
        sProject = project;
        project.extensions.create("beaver", Beaver)
    }

    static void registerTransform(Transform transform, Objects... dependency) {
        def android = sProject.extensions.getByType(AppExtension)
        android.registerTransform(transform, dependency)
    }

    public static Beaver getBeaver() {
        return sProject.beaver;
    }

    public static List<ClassIdentifier> getIncludeClassIdentifier() {

        if (sIncludeIdentifiers == null) {
            Beaver beaver = getBeaver();
            sIncludeIdentifiers = new ArrayList<>()
            beaver.includeClasses.each {
                String textureIdentifier ->
                    Logger.logInfo("=================== texture ${textureIdentifier}")
                    if (textureIdentifier.startsWith(REGEX_PREFIX)) {
                        String regex = textureIdentifier.substring(REGEX_PREFIX.length())
                        Logger.logInfo("===================${regex}")
                        if (StringUtils.isBlank(regex)) {
                            Logger.logWarn("warning: can't accept: ${textureIdentifier}")
                            return
                        }

                        sIncludeIdentifiers.add(new RegexClassIdentifier(regex))
                    } else {
                        sIncludeIdentifiers.add(new FullClassIdentifier(textureIdentifier))
                    }
            }
        }

        return sIncludeIdentifiers;
    }

    public static HashSet<HookUnit> getHookUnits() {
        return getBeaver().hookUnits;
    }
}
