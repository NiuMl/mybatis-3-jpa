package org.apache.ibatis.binding.jpa.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * User:niumengliang
 * Date:2024/11/27
 * Time:15:01
 * 字段别名
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaAlias {
    String value();
}
