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
import org.apache.ibatis.binding.jpa.processor.annotations.JpaId;
import org.apache.ibatis.binding.jpa.utils.ClassUtil;
import org.apache.ibatis.binding.jpa.utils.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 * @author niumengliang Date:2023/12/23 Time:14:44
 */
public class SpecificUpdateProcessor extends ProcessorParent {

  private static final Log log = LogFactory.getLog(SpecificUpdateProcessor.class);

  @Override
  public String process(Class<?> mapperInterface, String methodName, Configuration configuration) {
    log.debug("The current class being processed is " + this.getClass().getName() + " \nThe class to be processed is 【"
      + mapperInterface.getName() + "】 Method:" + methodName);
    JpaTable jpaTable = mapperInterface.getAnnotation(JpaTable.class);
    if (Objects.isNull(jpaTable))
      return null;
    Method[] methods = mapperInterface.getMethods();
    //根据methodName找到真实的Method
    Method method2 = Arrays.stream(methods).filter(m -> m.getName().equals(methodName))
      .findFirst().orElseThrow(() -> new BindingException("The new method:" + method + " not find in" + mapperInterface.getName()));

    Parameter[] parameters = method2.getParameters();
    if (parameters.length != 1) return null;
    Parameter param = parameters[0];
    Type paramType = param.getParameterizedType();
    //判断入参类型 如果不是类  就直接报错得了，这个类指的是普通类，不是list map 啥的,更新只更新一个
    if(!(paramType instanceof Class<?> paramClass)){
      throw new BindingException("the class:"+mapperInterface.getName()+"  method:"+methodName+"  param:"+param.getName()+" must be a class");
    }
    //找入参的别名  有就用  没有就不用
    String paramName = getMethodParameterAnnotations(method2);

    String useName = Objects.isNull(paramName) ? param.getName() : paramName;

    List<Field> allFields = ClassUtil.getAllFields(paramClass);
    Field hasIdField = allFields.stream().filter(a -> a.isAnnotationPresent(JpaId.class)).findFirst().orElseThrow(() ->
      new BindingException("the entity must have a field with @JpaId annotation in class【" + paramClass.getName() + "】"));

    List<Field> collect = allFields.stream().filter(a -> !a.isAnnotationPresent(JpaId.class)).toList();
    String ifCon = collect.stream().map(a -> {
      String tName = Objects.isNull(useName) ? a.getName() : useName + "." + a.getName();
      String temp2 = StringUtils.humpToLine(a.getName()) + " = #{" + tName + "},";
      String temp = null;
      if (a.getType() == String.class) {
        temp = tName + " != null and " + tName + " != ''";
      } else {
        temp = tName + " != null ";
      }
      return JpaXml.assembleTempUpdateSql(temp, temp2);
    }).collect(Collectors.joining("\n"));
    String t = Objects.isNull(useName) ? hasIdField.getName() : useName + "." + hasIdField.getName();
    String conditionWhere = StringUtils.humpToLine(hasIdField.getName()) + " = #{" + t + "}";
    //sql拼接
    String tempXml = JpaXml.assembleUpdateSql(methodName, jpaTable.value(), ifCon, mapperInterface.getName(), conditionWhere);
    System.out.println(tempXml);
    log.debug("The current xml sql is " + tempXml);
    parse(tempXml, configuration);
    return tempXml;
  }
}
