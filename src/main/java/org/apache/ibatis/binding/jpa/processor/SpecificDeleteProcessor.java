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
package org.apache.ibatis.binding.jpa.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.jpa.JpaTable;
import org.apache.ibatis.binding.jpa.handler.ClassReturnTypeAndInput;
import org.apache.ibatis.binding.jpa.handler.JpaXml;
import org.apache.ibatis.binding.jpa.processor.annotations.JpaId;
import org.apache.ibatis.binding.jpa.utils.ClassUtil;
import org.apache.ibatis.binding.jpa.utils.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

/***
 * @author niumengliang Date:2023/12/23 Time:14:44
 */
public class SpecificDeleteProcessor extends ProcessorParent {

  private static final Log log = LogFactory.getLog(SpecificDeleteProcessor.class);

  /**
   * 判断删除方法有没有参数 没有就全删 不带where条件 入参如果是一个类class，需要反射里面的ID，没有就爆炸 方法如果带有By 就是组装where条件 delete from table delete from table
   * where id = #{id} delete from table where xx = xx and xxx= xxx
   */
  @Override
  public String process(Class<?> mapperInterface, String methodName, Configuration configuration) {
    log.debug("The current class being processed is " + this.getClass().getName() + " \nThe class to be processed is 【"
        + mapperInterface.getName() + "】 Method:" + methodName);
    JpaTable jpaTable = mapperInterface.getAnnotation(JpaTable.class);
    if (Objects.isNull(jpaTable))
      return null;
    Method[] methods = mapperInterface.getMethods();
    // 根据methodName找到真实的Method
    Method method2 = Arrays.stream(methods).filter(m -> m.getName().equals(methodName)).findFirst().orElseThrow(
        () -> new BindingException("The new method:" + method + " not find in" + mapperInterface.getName()));
    Parameter[] parameters = method2.getParameters();
    // 没有入参的时候 就是干掉所有
    if (parameters.length == 0) {
      return JpaXml.assembleDeleteSql(methodName, jpaTable.value(), mapperInterface.getName(), "");
    }
    // 有入参的时候 判断入参个数 和类型
    // 所以这个删除只支持 delete(Bean) Bean里面必须要有@JpaId注解，deleteByIdXXX
    // 下面这个判断的意思是入参只有一个且是一个对象（非基本类型和其包装类）的时候，取对应对象里面有@JpaId的字段，没有就爆炸
    String whereCondition = "";
    if (parameters.length == 1 && !ClassUtil.judgeClassType(parameters[0].getType())) {
      System.out.println("入参是非基本类型");
      List<Field> allFields = ClassUtil.getAllFields(parameters[0].getType());
      Optional<Field> first = allFields.stream().filter(a -> a.isAnnotationPresent(JpaId.class)).findFirst();
      // if (first.isEmpty())
      // throw new BindingException("The new method:" + method2 + " not find @JpaId in" + mapperInterface.getName());
      // String id = first.get().getName();
      String id = first.orElseThrow(
          () -> new BindingException("The new method:" + method2 + " not find @JpaId in" + mapperInterface.getName()))
          .getName();
      whereCondition = " where " + StringUtils.humpToLine(id) + " = #{" + id + "}";
    } else {
      String[] mn = splitMethod(methodName, BY);
      if (Objects.isNull(mn) || mn.length != 2)
        return null;
      ClassReturnTypeAndInput crt = getClassReturnTypeAndInput(mapperInterface, methodName);
      // 查询条件切割
      String[] split = splitMethod(mn[1], ORDERBY);
      List<String> attrs = getAttrsNotToLine(split[0]);
      whereCondition = getWhereCondition(crt.getInputs(), attrs);
    }
    String tempXml = JpaXml.assembleDeleteSql(methodName, jpaTable.value(), mapperInterface.getName(), whereCondition);
    System.out.println(tempXml);
    log.debug("The current xml sql is " + tempXml);
    parse(tempXml, configuration);
    return tempXml;
  }

  // public static void main(String[] args) {
  // Class i = int.class;
  // System.out.println(i);
  // System.out.println(i.isPrimitive());
  // UserInfo ui = new UserInfo();
  // System.out.println(ui.getClass().isPrimitive());
  //
  // Integer ii = Integer.valueOf("11");
  // System.out.println(ii.getClass().isPrimitive());
  // System.out.println(ii.getClass() == Integer.class);
  // }
}
