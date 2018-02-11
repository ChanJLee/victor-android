package com.shanbay.beaver.asm;

import com.shanbay.beaver.log.Logger;
import com.shanbay.beaver.misc.Const;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by chan on 2017/6/14.
 */
public abstract class BaseMethodVisitor extends MethodVisitor {
    private boolean mIgnore = false;

    public BaseMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (Const.IGNORE_ANNOTATION_DESC == desc) {
            mIgnore = true;
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitCode() {
        if (!mIgnore) {
            hook();
            Logger.logInfo("modified");
        }

        super.visitCode();
    }

    protected abstract void hook();
}
