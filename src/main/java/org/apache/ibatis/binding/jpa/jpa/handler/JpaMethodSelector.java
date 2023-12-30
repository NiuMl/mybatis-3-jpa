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
package org.apache.ibatis.binding.jpa.jpa.handler;

import org.apache.ibatis.binding.BindingException;

import java.util.Arrays;

/***
 * User:niumengliang Date:2023/12/22 Time:16:43
 */
public enum JpaMethodSelector {

  SELECT("select"), QUERY("query"), GET("get"), FIND("find"), DELETE("delete"), DEL("DEL"), UPDATE("update"),
  INSERT("insert"), SAVE("save"), ADD("add"),;

  String value;

  JpaMethodSelector(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static JpaMethodSelector getByValue(String value) {
    return Arrays.stream(JpaMethodSelector.values()).filter(a -> value.startsWith(a.getValue())).findFirst()
        .orElseThrow(
            () -> new BindingException("The current method does not have clear operational instructions:" + value));
  }

  public static String handlerMethodName(String methodName) {
    JpaMethodSelector byValue = getByValue(methodName);
    return methodName.substring(byValue.value.length());
  }

}
