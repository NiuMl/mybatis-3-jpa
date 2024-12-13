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
package org.apache.ibatis.binding.jpa;

import java.util.HashSet;
import java.util.Set;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.jpa.handler.JpaMethodSelector;
import org.apache.ibatis.binding.jpa.processor.*;
import org.apache.ibatis.session.Configuration;

/***
 * @author niumengliang Date:2023/12/20 Time:15:45
 */
public class JpaMethod {

  private static ProcessorParent judgeAchieve(String method) {
    JpaMethodSelector jms = JpaMethodSelector.getByValue(method);
    switch (jms) {
      case SELECT, QUERY, GET, FIND -> {
        method = method.substring(jms.getValue().length());
        return new SpecificSelectProcessor(method);
      }
      case DELETE, DEL -> {
        return new SpecificDeleteProcessor();
      }
      case UPDATE -> {
        return new SpecificUpdateProcessor();
      }
      case INSERT, SAVE, ADD -> {
        return new SpecificInsertProcessor();
      }
    }
    throw new BindingException("The method " + method + " is not supported");
  }

  private static Set<String> initedMethods = new HashSet<>();

  public static void init(Class<?> mapperInterface, String methodName, String statementId,
      Configuration configuration) {
    if (initedMethods.contains(statementId))
      return;
    initedMethods.add(statementId);
    ProcessorParent pp = judgeAchieve(methodName);
    pp.process(mapperInterface, methodName, configuration);
  }
}
