package com.shanbay.beaver.data

import org.objectweb.asm.Opcodes

/**
 * Created by chan on 2017/6/1.
 */
public class HookEvent {
    public static final HookEvent VIEW_ON_CLICKED = new HookEvent(
            "android/view/View\$OnClickListener",
            Opcodes.ACC_PUBLIC,
            "onClick",
            "(Landroid/view/View;)V"
    )

    public static final HookEvent CHECKBOX_ON_CHECK_CHANGED = new HookEvent(
            "android/widget/CompoundButton\$OnCheckedChangeListener",
            Opcodes.ACC_PUBLIC,
            "onCheckedChanged",
            "(Landroid/widget/CompoundButton;Z)V"
    )

    private String mIdentifier
    private int mAccess
    private String mName
    private String mDesc

    public HookEvent(String identifier, int access, String name, String desc) {
        this.mIdentifier = identifier
        this.mAccess = access
        this.mName = name
        this.mDesc = desc
    }

    String getIdentifier() {
        return mIdentifier
    }

    int getAccess() {
        return mAccess
    }

    String getName() {
        return mName
    }

    String getDesc() {
        return mDesc
    }
}