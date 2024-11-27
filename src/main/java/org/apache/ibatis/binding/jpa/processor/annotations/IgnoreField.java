package org.apache.ibatis.binding.jpa.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * User:niumengliang
 * Date:2024/11/27
 * Time:14:40
 * 要忽略的字段标注字段
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreField {
}
