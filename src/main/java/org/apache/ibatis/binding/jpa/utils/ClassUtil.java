/*
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding.jpa.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * @author niumengliang Date:2024/11/27 Time:14:37
 */
public class ClassUtil {
  public static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fieldList = new ArrayList<>();
    while (clazz != null) {
      fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fieldList.stream().filter(a -> !a.getName().equals("serialVersionUID")).collect(Collectors.toList());
  }

  // 判断是不是基本类型和其对应的包装类
  public static boolean judgeClassType(Class<?> o) {
    return o.isPrimitive() || o == Byte.class || o == Short.class || o == Integer.class || o == Long.class
        || o == Float.class || o == Double.class || o == Boolean.class;
  }
}
