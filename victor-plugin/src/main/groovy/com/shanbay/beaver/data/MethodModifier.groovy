package com.shanbay.beaver.data

import org.objectweb.asm.MethodVisitor;

/**
 * Created by chan on 2017/6/21.
 */
public abstract class MethodModifier {
    abstract void modify(MethodVisitor mv);
}
