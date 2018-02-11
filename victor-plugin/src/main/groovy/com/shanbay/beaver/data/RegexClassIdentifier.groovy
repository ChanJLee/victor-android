package com.shanbay.beaver.data;

import java.util.regex.Pattern;

/**
 * Created by chan on 2017/6/1.
 */
public class RegexClassIdentifier extends ClassIdentifier {

    private Pattern mPattern;

    public RegexClassIdentifier(String identifier) {
        super(identifier);
        mPattern = Pattern.compile(identifier);
    }

    @Override
    public boolean match(String className) {
        return mPattern.matcher(className).find();
    }
}
