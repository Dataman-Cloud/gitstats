package com.dataman.gitstats.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: TODO()
 * @author biancl
 * @date 2017-10-12 15:04
 * @Copyright © 2017北京数人科技有限公司
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {

}
