package com.shanbay.beaver.data

import com.shanbay.beaver.asm.BaseMethodVisitor
import com.shanbay.beaver.misc.Const
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;

/**
 * Created by chan on 2017/6/2.
 */
public class HookUnit {

    public static final HookUnit OnClickedListenerHookUnit = new HookUnit(HookEvent.VIEW_ON_CLICKED, new MethodModifier() {
        @Override
        void modify(MethodVisitor mv) {
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Const.SDK_OWNER, "onClick", "(Landroid/view/View;)V", false)
        }
    })

    public static final HookUnit OnCheckedListenerHookUnit = new HookUnit(HookEvent.CHECKBOX_ON_CHECK_CHANGED, new MethodModifier() {
        @Override
        void modify(MethodVisitor mv) {
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitIntInsn(Opcodes.ALOAD, 2)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Const.SDK_OWNER, "onCheckedChanged", "(Landroid/widget/CompoundButton;Z)V", false)
        }
    })

    private HookEvent mEvent;
    private MethodModifier mMethodModifier;

    HookUnit(HookEvent mEvent, MethodModifier methodModifier) {
        this.mEvent = mEvent
        this.mMethodModifier = methodModifier
    }

    public boolean accept(int access, String name, String desc, String signature, String[] exceptions) {
        return (mEvent.getAccess() & access) != 0 &&
                StringUtils.equals(mEvent.getName(), name) &&
                StringUtils.equals(mEvent.getDesc(), desc);
    }

    public MethodVisitor hook(MethodVisitor methodVisitor) {
        return new BaseMethodVisitor(methodVisitor) {

            @Override
            protected void hook() {
                mMethodModifier.modify(mv)
            }
        }
    }

    HookEvent getEvent() {
        return mEvent
    }
}
