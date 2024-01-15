package org.apache.ibatis.binding.jpa.processor.wc;

import org.apache.ibatis.binding.jpa.StringUtils;

import java.util.Arrays;
import java.util.function.BiFunction;

/***
 * @author niumengliang
 * Date:2023/12/30
 * Time:10:25
 */
public enum WhereConditionEnums {

  EQ("-", (s, ss) -> " " + s + " = #{" + ss + "} "),
  NOT_EQ("Not", (s, ss) -> " " + s + " <![CDATA[ <>  ]]> #{" + ss + "} "),
  NOT_LIKE("NotLike", (s, ss) -> " " + s + " not like CONCAT('%',#{" + ss + "},'%' "),
  LIKE("Like", (s, ss) -> " " + s + " like CONCAT('%',#{" + ss + "},'%' "),
  IS_NOT_NULL("IsNotNull", (s, ss) -> " " + s + " is not null "),
  LESS_THAN("LessThan", (s, ss) -> " " + s + " <![CDATA[ < ]]> #{" + ss + "}"),
  LESS_THAN_EQUAL("LessThanEqual", (s, ss) -> " " + s + " <![CDATA[ <= ]]> #{" + ss + "}"),
  GREATER_THAN("GreaterThan", (s, ss) -> " " + s + " <![CDATA[ > ]]> #{" + ss + "}"),
  GREATER_THAN_EQUAL("GreaterThanEqual", (s, ss) -> " " + s + " <![CDATA[ >= ]]> #{" + ss + "}"),
  IS_NULL("IsNull", (s, ss) -> " " + s + " is null "),
  NOT_NULL("NotNull", (s, ss) -> " " + s + " is not null "),
  IN("In", (s, ss) -> " " + s + " in \n    <foreach collection=\"" + ss + "\" item=\"val\" index=\"index\" " +
    "open=\"(\" close=\")\" separator=\",\"> \n" +
    "      #{val} \n" +
    "   </foreach>"),
  NOT_IN("NotIn", (s, ss) -> " " + s + " not in \n <foreach collection=\"" + ss + "\" item=\"val\" index=\"index\" " +
    "open=\"(\" close=\")\" separator=\",\"> \n" +
    "#{val} \n" +
    "</foreach>"),
//  OR("or"),
//  NOT("not"),
//  BETWEEN("between"),
//  NOT_BETWEEN("not between"),

  ;

  String wc;
  BiFunction<String, String, String> wcFun;

  WhereConditionEnums(String wc, BiFunction<String, String, String> wcFun) {
    this.wc = wc;
    this.wcFun = wcFun;
  }

  public static String getWcSql(String s,String ss){
    WhereConditionEnums wce = Arrays.stream(WhereConditionEnums.values()).filter(a->s.endsWith(a.wc))
      .findFirst().orElse(EQ);
    return wce.wcFun.apply(StringUtils.humpToLine(s.substring(0,s.length()-wce.wc.length())),ss);
  }

//  public static void main(String[] args) {
//    String str = "IdIn";
//    String s= "In";
//    System.out.println(str.substring(0,str.length()-s.length()));
//  }
}
