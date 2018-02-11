package com.shanbay.beaver

import com.shanbay.beaver.data.InternalStorage
import com.shanbay.beaver.data.ProjectHelper
import com.shanbay.beaver.log.Logger
import com.shanbay.beaver.tramsform.ShanbayInjectTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by chan on 2017/6/1.
 */
class ShanbayHeaverPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        Logger.init(project)
        ProjectHelper.init(project)
        ProjectHelper.registerTransform(new ShanbayInjectTransform())
        Logger.logInfo("register heaver")
        InternalStorage.init(project)
    }
}
