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

}