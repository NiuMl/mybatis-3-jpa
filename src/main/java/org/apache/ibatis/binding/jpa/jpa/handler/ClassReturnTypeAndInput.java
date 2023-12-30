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

import java.util.Arrays;

/***
 * @author niumengliang Date:2023/12/23 Time:13:48
 */
public class ClassReturnTypeAndInput {

  private String returnTypeName;

  private String[] inputs;

  public String getReturnTypeName() {
    return returnTypeName;
  }

  public void setReturnTypeName(String returnTypeName) {
    this.returnTypeName = returnTypeName;
  }

  public String[] getInputs() {
    return inputs;
  }

  public void setInputs(String[] inputs) {
    this.inputs = inputs;
  }

  @Override
  public String toString() {
    return "ClassReturnTypeAndInput{" + "returnTypeName='" + returnTypeName + '\'' + ", inputs="
        + Arrays.toString(inputs) + '}';
  }
}
