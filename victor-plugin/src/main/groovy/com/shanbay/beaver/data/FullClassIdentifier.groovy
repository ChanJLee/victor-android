package com.shanbay.beaver.data;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by chan on 2017/6/1.
 */
public class FullClassIdentifier extends ClassIdentifier {

    public FullClassIdentifier(String identifier) {
        super(identifier);
    }

    @Override
    public boolean match(String className) {
        return StringUtils.equals(mIdentifier, className);
    }
}
