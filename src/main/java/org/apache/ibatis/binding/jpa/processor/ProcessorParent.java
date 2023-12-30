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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.jpa.StringUtils;
import org.apache.ibatis.binding.jpa.handler.ClassReturnTypeAndInput;
import org.apache.ibatis.binding.jpa.processor.wc.WhereConditionEnums;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

/***
 * @author niumengliang Date:2023/12/23 Time:14:43
 */
public abstract class ProcessorParent implements ProcessorInterface {

  private static final Log log = LogFactory.getLog(ProcessorParent.class);

  protected final String SELECT = "select ";
  protected final String FROM = " from ";
  protected final String AND = " and ";
  protected final String WHERE = " where ";
  protected final String BY = "By";

  protected String method;

  /**
   * 1、解析方法，判断是使用哪个XXXProcessor 2、解析出sql中from前面的部分 3、解析出sql中的要from的表 4、解析出sql中的where条件
   */

  protected String getAttrs(String str, String join) {
    return Arrays.stream(str.split("And")).map(StringUtils::humpToLine).collect(Collectors.joining(join));
  }

  public static List<String> getAttrs(String str) {
    return Arrays.stream(str.split("And")).map(StringUtils::humpToLine).collect(Collectors.toList());
  }
  public static List<String> getAttrsNotToLine(String str) {
    return Arrays.stream(str.split("And")).collect(Collectors.toList());
  }

  protected String getSelectFor(String method) {
    return (StringUtils.isEmpty(method) ? "*" : getAttrs(method, ","));
  }

  private static String getReturnType(Method method, String methodName) {
    Type genericReturnType = method.getGenericReturnType();
    // 获取实际返回的参数名
    String returnTypeName = genericReturnType.getTypeName();
    // System.out.println(methodName + "的返回参数是：" + returnTypeName);
    if (genericReturnType instanceof ParameterizedType pt) {
      Type[] actualTypeArguments = pt.getActualTypeArguments();
      for (Type type : actualTypeArguments) {
        // 强转
        Class<?> actualTypeArgument = (Class<?>) type;
        // 获取实际参数的类名
        String name = actualTypeArgument.getName();
        // System.out.println(methodName + "的返回值类型是参数化类型，其类型为：" + name);
        return name;
      }
    } else {
      // 不是参数化类型,直接获取返回值类型
      Class<?> returnType = method.getReturnType();
      // 获取返回值类型的类名
      String name = returnType.getName();
      // System.out.println(methodName + "的返回值类型不是参数化类型其类型为：" + name);
      return name;
    }
    return null;
  }

  private static String[] getInputs(Method method, String methodName) {

    String[] inputs = new String[0];
    Parameter[] parameters = method.getParameters();
    if (Objects.nonNull(parameters)) {
      inputs = new String[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        inputs[i] = parameters[i].getName();
      }
      Arrays.stream(parameters).forEach(a -> {
        Class<?> type = a.getType();
        String name = a.getName();
      });
    }
    return inputs;
  }

  public static ClassReturnTypeAndInput getClassReturnTypeAndInput(Class c, String methodName) {
    ClassReturnTypeAndInput crti = new ClassReturnTypeAndInput();
    Method[] methods = c.getMethods();
    Method method = Arrays.stream(methods).filter(a -> a.getName().equals(methodName)).findFirst()
        .orElseThrow(() -> new BindingException("The method (" + methodName + ") is not exist!"));
    crti.setReturnTypeName(getReturnType(method, methodName));
    crti.setInputs(getInputs(method, methodName));
    return crti;
  }

//  private final BiFunction<String,String,String> handlerWhereCondition = (wc, parameter)->{
//    if(wc.endsWith("NotList")){
//
//    }else if(wc.endsWith("Like")){
//
//    } else if (wc.endsWith("NotIn")) {
//
//    }else if(wc.endsWith("In")){
//
//    }
//  };

  protected String getWhereCondition(List<String> attrs, String[] arr) {
    AtomicInteger i = new AtomicInteger();
    return attrs.stream().map(a -> {
      a = WhereConditionEnums.getWcSql(a,arr[i.get()]);
      i.getAndIncrement();
      return a;
    }).collect(Collectors.joining(AND));
  }

  protected String[] splitMethod(String str, String target) {
    log.debug("splitMethod:" + str + " target:" + target);
    String[] arr = new String[2];
    char[] by = target.toCharArray();
    char[] charArray = str.toCharArray();
    int byB = 0, byE = 0;
    for (int i = 0; i < charArray.length; i++) {
      boolean b = true;
      if (by[0] == charArray[i]) {
        byB = i;
        for (int i1 = 1; i1 < by.length; i1++) {
          if (charArray.length >= i + i1 && by[i1] != charArray[i + i1]) {
            b = false;
            break;
          }
        }
        if (b) {
          byE = i + by.length - 1;
        }
      }
    }
    if ((byB == 0 && byE == 0) || (byB != 0 && byE == 0))
      return null;
    arr[0] = str.substring(0, byB);
    arr[1] = str.substring(byE + 1);
    return arr;
  }

  protected void parse(String xml, Configuration configuration) {
    try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes())) {
      XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, null,
          configuration.getSqlFragments());
      mapperParser.parse();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
