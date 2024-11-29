/*
 *    Copyright 2009-2023 the original author or authors.
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
package org.apache.ibatis.binding.jpa.processor;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.jpa.JpaTable;
import org.apache.ibatis.binding.jpa.handler.JpaXml;
import org.apache.ibatis.binding.jpa.processor.annotations.IgnoreField;
import org.apache.ibatis.binding.jpa.processor.annotations.JpaAlias;
import org.apache.ibatis.binding.jpa.processor.annotations.JpaId;
import org.apache.ibatis.binding.jpa.utils.ClassUtil;
import org.apache.ibatis.binding.jpa.utils.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 * @author niumengliang Date:2023/12/23 Time:14:44
 */
public class SpecificInsertProcessor extends ProcessorParent {
  private static final Log log = LogFactory.getLog(SpecificInsertProcessor.class);




  @Override
  public String process(Class<?> mapperInterface, String methodName, Configuration configuration) {
    log.debug("The current class being processed is " + this.getClass().getName() + " \nThe class to be processed is 【"
      + mapperInterface.getName() + "】 Method:" + methodName);
    JpaTable jpaTable = mapperInterface.getAnnotation(JpaTable.class);
    if (Objects.isNull(jpaTable))
      return null;
    Method[] methods = mapperInterface.getMethods();
    Method method2 = Arrays.stream(methods).filter(m -> m.getName().equals(methodName))
      .findFirst().orElseThrow(() -> new BindingException("The new method:" + method + " not find in" + mapperInterface.getName()));
    //入参对象  只取第一个
    Class<?> parameterType = method2.getParameterTypes()[0];
    //判断是单个保存还是批量的 是不是集合
    boolean b = parameterType == List.class;
    String fieldName = null;
    if (b) {
      Object[] tmp = getParameterType(method2);
      if (tmp != null) {
        parameterType = (Class<?>) tmp[0];
        fieldName = (String) tmp[1];
      }
    }
    //找入参的别名  有就用  没有就不用
    String paramName = getMethodParameterAnnotations(method2);

    //获取所有字段 并过滤掉有标注JpaId和IgnoreField的字段  插入的时候不使用
    List<Field> allFields = ClassUtil.getAllFields(parameterType).stream().filter(a ->
        !a.isAnnotationPresent(JpaId.class) && !a.isAnnotationPresent(IgnoreField.class))
      .toList();
    //获取插入字段
    String sqlFields = allFields.stream().map(a -> {
      if (a.isAnnotationPresent(JpaAlias.class)) {
        JpaAlias ja = a.getAnnotation(JpaAlias.class);
        if (Objects.nonNull(ja.value())) {
          return ja.value();
        }
      }
      return StringUtils.humpToLine(a.getName());
    }).map(a -> "`" + a + "`").collect(Collectors.joining(","));

    //获取插入值  也就是#{articleTitle}这样的形式
    String sqlShapValue = allFields.stream().map(a -> {
      String s = b ? "entity." + a.getName() : (Objects.isNull(paramName) ? "" : paramName + ".") + a.getName();
      return "#{" + s + "}";
    }).collect(Collectors.joining(","));

    //sql拼接
    String tempXml = null;
    if (!b) {
      String sql = INSERT + jpaTable.value() + "(" + sqlFields + ") values(" + sqlShapValue + ")";
      //临时xml sql
      tempXml = JpaXml.assembleInsetSql(methodName, sql, mapperInterface.getName());
    } else {
      tempXml = JpaXml.assembleInsetBatchSql(methodName, sqlShapValue, mapperInterface.getName(), fieldName, jpaTable.value(), sqlFields);
    }
    System.out.println(tempXml);
    log.debug("The current xml sql is " + tempXml);
    parse(tempXml, configuration);
    return tempXml;
  }

//  public static void main(String[] args) throws ClassNotFoundException {
//    List<String> list = new ArrayList<>();
//    Type genericSuperclass = list.getClass().getGenericSuperclass();
//    System.out.println(genericSuperclass.getTypeName());
//    Class<?> c = Class.forName("com.niuml.UserInfo");
//    List<Field> allFields = ClassUtil.getAllFields(c);
//    allFields.forEach(a -> {
//      System.out.println(a.getName());
//      JpaId jpa = a.getAnnotation(JpaId.class);
//      if (Objects.nonNull(jpa)) {
//        System.out.println(a.getName() + "有 JpaId");
//      }
//      IgnoreField igf = a.getAnnotation(IgnoreField.class);
//      if (Objects.nonNull(igf))
//        System.out.println(a.getName() + "有 IgnoreField");
//      JpaAlias ja = a.getAnnotation(JpaAlias.class);
//      if (Objects.nonNull(ja)) {
//        System.out.println(a.getName() + "有 JpaAlias");
//        if (Objects.nonNull(ja.value())) {
//          System.out.println(a.getName() + "有 JpaAlias value=" + ja.value());
//        }
//      }
//    });
//  }
}
