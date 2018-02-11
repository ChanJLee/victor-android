package com.shanbay.beaver.asm

import com.shanbay.beaver.data.HookUnit
import com.shanbay.beaver.log.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by chan on 2017/6/2.
 */
public class BeaverClassVisitor extends ClassVisitor {

    List<HookUnit> mHookUnits = new ArrayList<>()
    private HookUnit mTargetHookUnit = null

    public BeaverClassVisitor(List<HookUnit> units, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        mHookUnits = units;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)

        for (HookUnit unit : mHookUnits) {
            if (unit.event.identifier == superName) {
                Logger.logInfo("catch class: $name super: $superName")
                mTargetHookUnit = unit;
                return
            }

            for (int i = 0; interfaces != null && i < interfaces.length; ++i) {
                if (unit.getEvent().identifier == interfaces[i]) {
                    Logger.logInfo("catch class: $name interface: ${interfaces[i]}")
                    mTargetHookUnit = unit
                    return
                }
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        if (mTargetHookUnit != null && mTargetHookUnit.accept(access, name, desc, signature, exceptions)) {
            Logger.logInfo("hook: $name $desc")
            methodVisitor = mTargetHookUnit.hook(methodVisitor);
        }
        return methodVisitor;
    }
}
