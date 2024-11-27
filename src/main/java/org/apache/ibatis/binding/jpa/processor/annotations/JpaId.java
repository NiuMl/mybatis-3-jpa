package org.apache.ibatis.binding.jpa.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * User:niumengliang
 * Date:2024/11/27
 * Time:13:44
 * 标记某个字段为主键  比如更新的时候用,插入的时候判断主键啥的
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaId {

  String value() default "";
}
