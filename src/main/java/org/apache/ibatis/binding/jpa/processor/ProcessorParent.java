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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.jpa.handler.ClassReturnTypeAndInput;
import org.apache.ibatis.binding.jpa.processor.wc.WhereConditionEnums;
import org.apache.ibatis.binding.jpa.utils.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

public abstract class ProcessorParent implements ProcessorInterface {

  private static final Log log = LogFactory.getLog(ProcessorParent.class);

  protected final String SELECT = "select ";
  protected final String INSERT = "insert into ";
  protected final String FROM = " from ";
  protected final String AND = " and ";
  protected static final String UP_AND = " And ";
  protected static final String UP_AND2 = "And";
  protected final String WHERE = " where ";
  protected final String BY = "By";
  protected final String ORDERBY = "OrderBy";

  protected String method;

  /**
   * 1、解析方法，判断是使用哪个XXXProcessor 2、解析出sql中from前面的部分 3、解析出sql中的要from的表 4、解析出sql中的where条件
   */

  protected String getAttrs(String str, String join) {
    return Arrays.stream(str.split(UP_AND2)).map(StringUtils::humpToLine).collect(Collectors.joining(join));
  }

  public static List<String> getAttrs(String str) {
    return Arrays.stream(str.split(UP_AND2)).map(StringUtils::humpToLine).collect(Collectors.toList());
  }

  /**
   * 将查询字符串分割成查询字段
   *
   * @param str
   *          查询字符串
   *
   * @return 切割后list
   */
  public static List<String> getAttrsNotToLine(String str) {
    return Arrays.stream(str.split(UP_AND2)).collect(Collectors.toList());
  }

  protected String getSelectFor(String method) {
    return (StringUtils.isEmpty(method) ? "*" : getAttrs(method, ","));
  }

  private static String getReturnType(Method method, String methodName) {
    Type genericReturnType = method.getGenericReturnType();
    // 获取实际返回的参数名
    // String returnTypeName = genericReturnType.getTypeName();
    // System.out.println(methodName + "的返回参数是：" + returnTypeName);
    if (genericReturnType instanceof ParameterizedType pt) {
      Type[] actualTypeArguments = pt.getActualTypeArguments();
      for (Type type : actualTypeArguments) {
        // 强转
        Class<?> actualTypeArgument = (Class<?>) type;
        // 获取实际参数的类名
        // System.out.println(methodName + "的返回值类型是参数化类型，其类型为：" + name);
        return actualTypeArgument.getName();
      }
    } else {
      // 不是参数化类型,直接获取返回值类型
      Class<?> returnType = method.getReturnType();
      // 获取返回值类型的类名
      // System.out.println(methodName + "的返回值类型不是参数化类型其类型为：" + name);
      return returnType.getName();
    }
    return null;
  }

  private static String[] getInputs(Method method, String methodName) {
    String[] inputs = new String[0];
    Parameter[] parameters = method.getParameters();
    if (Objects.nonNull(parameters)) {
      inputs = new String[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        Param alien = parameters[i].getAnnotation(Param.class);
        inputs[i] = (Objects.isNull(alien) || Objects.isNull(alien.value()) || alien.value().isEmpty())
            ? parameters[i].getName() : alien.value();
      }
    }
    return inputs;
  }

  public static ClassReturnTypeAndInput getClassReturnTypeAndInput(Class<?> c, String methodName) {
    ClassReturnTypeAndInput crti = new ClassReturnTypeAndInput();
    Method[] methods = c.getMethods();
    Method method = Arrays.stream(methods).filter(a -> a.getName().equals(methodName)).findFirst()
        .orElseThrow(() -> new BindingException("The method (" + methodName + ") is not exist!"));
    crti.setReturnTypeName(getReturnType(method, methodName));
    crti.setInputs(getInputs(method, methodName));
    return crti;
  }

  /**
   * 循环“查询值字段”去匹配“查询字段”
   *
   * @param arr
   *          查询值字段
   * @param attrs
   *          查询字段
   *
   * @return 拼接好的where
   */
  protected String getWhereCondition(String[] arr, List<String> attrs) {
    List<String> reList = new ArrayList<>();
    for (int i = 0; i < arr.length; i++) {
      String s = attrs.get(i);
      WhereConditionEnums wcle = WhereConditionEnums.getWcSql(s);
      String ss = wcle.getPlus() > 0 ? arr[i] + "," + arr[i + 1] : arr[i];
      String sb = s.substring(0,
          s.length() - (wcle == WhereConditionEnums.EQ ? wcle.getWc().length() - 1 : wcle.getWc().length()));
      reList.add(wcle.getWcFun().apply(StringUtils.humpToLine(sb), ss));
      // 可能会有一个属性要使用多个参数的时候 比如between
      i += wcle.getPlus();
    }
    return reList.isEmpty() ? "" : WHERE + String.join(AND, reList);
  }

  /***
   * by niuml 自实现字符串分隔。 MD 写的时候我和上帝都知道这是写的啥玩意，现在只有上帝知道 2024 11 29
   */
  protected String[] splitMethod(String str, String target) {
    log.debug("splitMethod:" + str + " target:" + target);
    String[] arr = new String[2];
    char[] by = target.toCharArray();
    char[] charArray = str.toCharArray();
    int byB = 0, byE = 0;
    char[] tmp = new char[str.length()];
    for (int i = 0; i < charArray.length; i++) {
      boolean b = true;
      byB = i;
      if (by[0] == charArray[i]) {
        tmp = new char[str.length()];
        for (int i1 = 1; i1 < by.length; i1++) {
          if (charArray.length >= i + i1 && by[i1] != charArray[i + i1]) {
            b = false;
            break;
          }
        }
        if (b) {
          byE = i + by.length - 1;
          break;
        }
      } else {
        tmp[i] = charArray[i];
      }
    }
    // if ((byB == 0 && byE == 0) || (byB != 0 && byE == 0)){
    // return null;
    // }
    arr[0] = str.substring(0, byB);
    if (byE == 0) {
      arr[0] = str.substring(0, byB + 1);
    }
    if (byE != 0)
      arr[1] = str.substring(byE + 1);
    return arr;
  }

  protected void parse(String xml, Configuration configuration) {
    try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes())) {
      UUID uuid = UUID.randomUUID();
      // by niuml 这之所有用了一个uuid，是因为.parse()方法里面有一个检查，如果当前类或者null已被加载过，就忽略了
      XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, uuid.toString(),
          configuration.getSqlFragments());
      mapperParser.parse();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // 获取方法的入参别名 主要在插入或者更新的时候使用 by niuml
  protected String getMethodParameterAnnotations(Method method) {
    AtomicReference<Param> param = new AtomicReference<>();
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    Arrays.stream(parameterAnnotations).filter(a -> {
      List<Annotation> collect = Arrays.stream(a).filter(b -> b instanceof Param).toList();
      return !collect.isEmpty();
    }).findFirst().ifPresent(a -> param.set((Param) a[0]));
    return param.get() == null ? null : param.get().value();
  }

  protected Object[] getParameterType(Method method2) {
    // 判断是单个保存还是批量的
    Parameter[] parameters = method2.getParameters();
    if (parameters.length != 1)
      return null;
    Parameter param = parameters[0];
    Type paramType = param.getParameterizedType();
    System.out.println(paramType);
    if (paramType instanceof Class<?>) {
      // System.out.println("单个");
      return new Object[] { paramType, param.getName() };
    } else if (paramType instanceof ParameterizedType pt) {
      // 非单个类的时候，param.getParameterizedType()返回的是ParameterizedTypeImpl这个类，
      // 这个是jdk自带，1.8之后因为模块化无法直接引入和获取，所以也就无法获取到Type的属性，
      // 但是ParameterizedTypeImpl又是实现了ParameterizedType这个类，这个是接口类，可以获取到属性 Oj8K
      // System.out.println("集合");
      Type actualTypeArgument = pt.getActualTypeArguments()[0];
      System.out.println(actualTypeArgument.getTypeName());
      Class<?> aClass;
      try {
        aClass = Class.forName(actualTypeArgument.getTypeName());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      String paramName = getMethodParameterAnnotations(method2);
      return new Object[] { aClass, Objects.isNull(paramName) ? param.getName() : paramName };
    }
    return null;
  }

}
