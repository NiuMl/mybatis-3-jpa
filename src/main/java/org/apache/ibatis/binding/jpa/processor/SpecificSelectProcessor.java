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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.ibatis.binding.jpa.JpaTable;
import org.apache.ibatis.binding.jpa.StringUtils;
import org.apache.ibatis.binding.jpa.handler.ClassReturnTypeAndInput;
import org.apache.ibatis.binding.jpa.handler.JpaMethodSelector;
import org.apache.ibatis.binding.jpa.handler.JpaXml;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

public class SpecificSelectProcessor extends ProcessorParent {
  private static final Log log = LogFactory.getLog(SpecificSelectProcessor.class);

  public SpecificSelectProcessor(String method) {
    super.method = method;
  }

  /**
   * 将查询方法处理成查询sql
   * @param mapperInterface mapper类
   * @param methodName 方法名
   * @param configuration mybatis的配置类
   * @return 生成好的sql
   */
  @Override
  public String process(Class<?> mapperInterface, String methodName, Configuration configuration) {
    log.debug("The current class being processed is " + this.getClass().getName() + " \nThe class to be processed is 【"
      + mapperInterface.getName() + "】 Method:" + methodName);
    JpaTable jpaTable = mapperInterface.getAnnotation(JpaTable.class);
    if (Objects.isNull(jpaTable))
      return null;
    String[] mn = splitMethod(methodName, BY);
    if (Objects.isNull(mn) || mn.length != 2)
      return null;
    ClassReturnTypeAndInput crt = getClassReturnTypeAndInput(mapperInterface, methodName);
    //查询条件切割
//    String[] split = mn[1].split(ORDERBY);
    String[] split = splitMethod(mn[1],ORDERBY);
    List<String> attrs = getAttrsNotToLine(split[0]);
//    String whereCondition = getWhereCondition(attrs, crt.getInputs());
    String whereCondition = getWhereCondition(crt.getInputs(), attrs);
    //order by
    String orderBy = "";
    if (split.length > 1) {
      orderBy = getOrderBy(split[1]);
    }
    String sql = SELECT + getSelectFor(JpaMethodSelector.handlerMethodName(mn[0])) + FROM + jpaTable.value() + whereCondition + orderBy;
    String xml = JpaXml.assembleSql(methodName, crt.getReturnTypeName(), sql, mapperInterface.getName());
    System.out.println(xml);
    parse(xml, configuration);
    return xml;
  }

  private String getOrderBy(String str) {
    if (StringUtils.isEmpty(str)) return "";
    String temp = Arrays.stream(str.split(UP_AND2)).map(a -> {
      String pix = "";
      String att = a;
      if (a.endsWith("Asc")) {
        att = a.substring(0, a.length() - 3);
        pix = " asc ";
      }
      if (a.endsWith("Desc")) {
        att = a.substring(0, a.length() - 4);
        pix = " desc ";
      }
      return StringUtils.humpToLine(att) + pix;
    }).collect(Collectors.joining(","));

    return " order by " + temp;
  }
}
