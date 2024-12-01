package org.apache.ibatis.binding.jpa.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * @author niumengliang
 * Date:2024/11/27
 * Time:14:37
 */
public class ClassUtil {
  public static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fieldList = new ArrayList<>();
    while (clazz != null){
      fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fieldList.stream().filter(a->!a.getName().equals("serialVersionUID")).collect(Collectors.toList());
  }

  //判断是不是基本类型和其对应的包装类
  public static boolean judgeClassType(Class<?> o ){
    return o.isPrimitive()
      || o == Byte.class
      || o == Short.class
      || o == Integer.class
      || o == Long.class
      || o == Float.class
      || o == Double.class
      || o == Boolean.class;
  }
}
