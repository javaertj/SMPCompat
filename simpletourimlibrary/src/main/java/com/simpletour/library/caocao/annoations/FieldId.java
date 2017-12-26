package com.simpletour.library.caocao.annoations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包名：com.simpletour.library.caocao.annoations
 * 描述：消息模型解析注解
 * 创建者：yankebin
 * 日期：2017/5/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldId {
    int value();
}