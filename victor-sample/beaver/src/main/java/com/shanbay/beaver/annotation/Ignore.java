package com.shanbay.beaver.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chan on 2017/6/14.
 */
@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.CLASS)
@DoNotModify
public @interface Ignore {
}
