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
package org.apache.ibatis.binding.jpa.processor.wc;

import java.util.Arrays;
import java.util.function.BiFunction;

/***
 * @author niumengliang Date:2023/12/30 Time:10:25
 */
public enum WhereConditionEnums {

  EQ("-", (s, ss) -> " " + s + " = #{" + ss + "} ", 0),
  NOT_EQ("Not", (s, ss) -> " " + s + " <![CDATA[ <>  ]]> #{" + ss + "} ", 0),
  NOT_LIKE("NotLike", (s, ss) -> " " + s + " not like CONCAT('%',#{" + ss + "},'%' ", 0),
  LIKE("Like", (s, ss) -> " " + s + " like CONCAT('%',#{" + ss + "},'%' ", 0),
  IS_NOT_NULL("IsNotNull", (s, ss) -> " " + s + " is not null ", 0),
  LESS_THAN("LessThan", (s, ss) -> " " + s + " <![CDATA[ < ]]> #{" + ss + "}", 0),
  LESS_THAN_EQUAL("LessThanEqual", (s, ss) -> " " + s + " <![CDATA[ <= ]]> #{" + ss + "}", 0),
  GREATER_THAN("GreaterThan", (s, ss) -> " " + s + " <![CDATA[ > ]]> #{" + ss + "}", 0),
  GREATER_THAN_EQUAL("GreaterThanEqual", (s, ss) -> " " + s + " <![CDATA[ >= ]]> #{" + ss + "}", 0),
  IS_NULL("IsNull", (s, ss) -> " " + s + " is null ", 0), NOT_NULL("NotNull", (s, ss) -> " " + s + " is not null ", 0),
  IN("In",
      (s, ss) -> " " + s + " in \n    <foreach collection=\"" + ss + "\" item=\"val\" index=\"index\" "
          + "open=\"(\" close=\")\" separator=\",\"> \n" + "      #{val} \n" + "   </foreach>",
      0),
  NOT_IN("NotIn",
      (s, ss) -> " " + s + " not in \n <foreach collection=\"" + ss + "\" item=\"val\" index=\"index\" "
          + "open=\"(\" close=\")\" separator=\",\"> \n" + "#{val} \n" + "</foreach>",
      0),
  // OR("or"),
  BETWEEN("Between", (s, ss) -> {
    String[] split = ss.split(",");
    return " " + s + " between #{" + split[0] + "} and #{" + split[1] + "} ";
  }, 1),
  // NOT_BETWEEN("not between"),

  ;

  final String wc;
  final int plus;
  final BiFunction<String, String, String> wcFun;

  WhereConditionEnums(String wc, BiFunction<String, String, String> wcFun, int plus) {
    this.wc = wc;
    this.wcFun = wcFun;
    this.plus = plus;
  }

  public static WhereConditionEnums getWcSql(String s) {
    return Arrays.stream(WhereConditionEnums.values()).filter(a -> s.endsWith(a.wc)).findFirst().orElse(EQ);
  }

  public String getWc() {
    return wc;
  }

  public int getPlus() {
    return plus;
  }

  public BiFunction<String, String, String> getWcFun() {
    return wcFun;
  }

  // public static void main(String[] args) {
  // String str = "IdIn";
  // String s= "In";
  // System.out.println(str.substring(0,str.length()-s.length()));
  // }
}
