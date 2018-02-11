package com.shanbay.beaver.data;

/**
 * Created by chan on 2017/6/1.
 */
public abstract class ClassIdentifier {

    protected String mIdentifier;

    public ClassIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public abstract boolean match(String className);
}
