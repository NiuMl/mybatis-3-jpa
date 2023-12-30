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

import java.util.List;
import java.util.Objects;

import org.apache.ibatis.binding.jpa.JpaTable;
import org.apache.ibatis.binding.jpa.handler.ClassReturnTypeAndInput;
import org.apache.ibatis.binding.jpa.handler.JpaMethodSelector;
import org.apache.ibatis.binding.jpa.handler.JpaXml;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

/***
 * @author niumengliang Date:2023/12/23 Time:14:44
 */
public class SpecificSelectProcessor extends ProcessorParent {
  private static final Log log = LogFactory.getLog(SpecificSelectProcessor.class);

  public SpecificSelectProcessor(String method) {
    super.method = method;
  }

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
    //todo 查询条件切割
    List<String> attrs = getAttrsNotToLine(mn[1]);
    String whereCondition = getWhereCondition(attrs, crt.getInputs());

    String sql = SELECT + getSelectFor(JpaMethodSelector.handlerMethodName(mn[0])) + FROM + jpaTable.value() + WHERE
        + whereCondition;

    String xml = JpaXml.assembleSql(methodName, crt.getReturnTypeName(), sql, mapperInterface.getName());
    System.out.println(xml);
    parse(xml, configuration);
    return xml;
  }
}
