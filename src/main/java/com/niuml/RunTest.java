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
package com.niuml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.ibatis.binding.jpa.utils.StringUtils;
import org.apache.ibatis.binding.jpa.handler.JpaMethodSelector;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class RunTest {

  public static String[] getMethodName(String str, String target) {
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
    // System.out.println(byB + "_" + byE);
    if ((byB == 0 && byE == 0) || (byB != 0 && byE == 0))
      return null;
    arr[0] = str.substring(0, byB);
    arr[1] = str.substring(byE + 1);
    return arr;
  }

  public static String getAttrs(String str, String join) {
    return Arrays.stream(str.split("And")).map(StringUtils::humpToLine).collect(Collectors.joining(join));
  }

  public static List<String> getAttrs(String str) {
    return Arrays.stream(str.split("And")).map(StringUtils::humpToLine).collect(Collectors.toList());
  }

  public static String getSelectSql(String str) {
    JpaMethodSelector JMS = JpaMethodSelector.getByValue(str);
    if (str.startsWith(JMS.getValue())) {
      str = str.substring(JMS.getValue().length());
    }

    return JMS.getValue() + " " + (StringUtils.isEmpty(str) ? "*" : getAttrs(str, ",")) + " from ";
  }

  public static String getConditionSql(String str) {
    return " where " + getAttrs(str, " = ? and ") + " = ? ";
  }

  private static String getReturnType(Method method, String methodName) {
    Type genericReturnType = method.getGenericReturnType();
    // 获取实际返回的参数名
    String returnTypeName = genericReturnType.getTypeName();
    System.out.println(methodName + "的返回参数是：" + returnTypeName);
    if (genericReturnType instanceof ParameterizedType pt) {
      Type[] actualTypeArguments = pt.getActualTypeArguments();
      for (Type type : actualTypeArguments) {
        // 强转
        Class<?> actualTypeArgument = (Class<?>) type;
        // 获取实际参数的类名
        String name = actualTypeArgument.getName();
        System.out.println(methodName + "的返回值类型是参数化类型，其类型为：" + name);
        return name;
      }
    } else {
      // 不是参数化类型,直接获取返回值类型
      Class<?> returnType = method.getReturnType();
      // 获取返回值类型的类名
      String name = returnType.getName();
      System.out.println(methodName + "的返回值类型不是参数化类型其类型为：" + name);
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
        System.out.println("参数名：" + name + "，类型：" + type.getName());
      });
    }
    return inputs;
  }

  // public static ClassReturnTypeAndInput getClassReturnTypeAndInput(Class c, String methodName) {
  // ClassReturnTypeAndInput crti = new ClassReturnTypeAndInput();
  // Method[] methods = c.getMethods();
  // Method method = Arrays.stream(methods).filter(a -> a.getName().equals(methodName)).findFirst()
  // .orElseThrow(() -> new BindingException("The method (" + methodName + ") is not exist!"));
  // crti.setReturnTypeName(getReturnType(method, methodName));
  // crti.setInputs(getInputs(method, methodName));
  // return crti;
  // }

  public static void main(String[] args) throws IOException {
    // String str = "selectByNameAndEnforceNumAndRealName";
    // String target = "By";
    // String[] methodName = getMethodName(str, target);
    // String selectSql = getSelectSql(methodName[0]);
    // JpaTable jpaTable = UserInfoMapper.class.getAnnotation(JpaTable.class);
    //// String meName = "selectByNameAndEnforceNumAndRealName";
    //// String meName = "selectByNameAndEnforceNumAndRealName";
    //
    // ClassReturnTypeAndInput classReturnTypeAndInput = getClassReturnTypeAndInput(UserInfoMapper.class, str);
    // System.out.println(classReturnTypeAndInput);
    //
    // List<String> attrs = getAttrs(methodName[1]);
    // AtomicInteger i = new AtomicInteger();
    // String collect = attrs.stream().map(a -> {
    // a = " " + a + " = #{" + classReturnTypeAndInput.getInputs()[i.get()] + "} ";
    // i.getAndIncrement();
    // return a;
    // }).collect(Collectors.joining(" and "));
    //
    // String sql = selectSql + jpaTable.value() + " where " + collect;
    // System.out.println("方法名称：" + str);
    // System.out.println("解析成sql：" + sql);

    // char[] charArray = str.toCharArray();
    // StringBuilder method = new StringBuilder();
    // String afterMethod = "";
    //
    // int byB = 0, byE = 0;
    //
    // int index = 0;
    // for (char c : charArray) {
    // System.out.println(c + "_" + (int) c + "_" + index++);
    // boolean b = true;
    // if ((int) c == by[0]) {
    // byB = index-1;
    // for (int i = 1; i < by.length; i++) {
    // int tmpCount = index + i;
    // if (charArray.length >= tmpCount) {
    // if (by[i] != charArray[index]) {
    // b = false;
    // }
    // }
    // }
    // if (b) {
    // System.out.println(method);
    // byE = index;
    // }
    // }
    // method.append(c);
    // }
    // System.out.println("byB:" + byB + " byE:" + byE);

    // select * from hbzf_user.hfd_user_info where user_name = #{s} and enforce_num = #{s1}
    // String str = "selectByUserNameAndEnforceName";
    // //
    // String[] split = str.split("By");
    // String method = split[0];

    // Class c = UserInfoMapper.class;
    // //获取注解
    // Annotation annotation = c.getAnnotation(JpaTable.class);
    // JpaTable jpaTable = annotation.annotationType().getAnnotation(JpaTable.class);
    // System.out.println(jpaTable);
    // c.getDeclaredAnnotation()
    // JpaTable jpaTable = UserInfoMapper.class.getAnnotation(JpaTable.class);
    // System.out.println(jpaTable.value());
    //
    // Class um = UserInfoMapper.class;
    // Class[] interfaces = um.getInterfaces();
    // for (Class aClass : interfaces) {
    // System.out.println(aClass);
    // Type[] genericInterfaces = aClass.getGenericInterfaces();
    // Type type = genericInterfaces[0];
    // if( type instanceof ParameterizedType ){
    // ParameterizedType pType = (ParameterizedType)type;
    // Type clazz = pType.getActualTypeArguments()[0];
    // System.out.println(clazz);
    // }
    // }

    String resource = "com/niuml/mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    // sqlSessionFactory.getConfiguration().addMapper(UserInfoMapper.class);
    try (SqlSession session = sqlSessionFactory.openSession()) {
      UserInfoMapper mapper = session.getMapper(UserInfoMapper.class);
      // UserInfo ui = mapper.selectByUserNameAndEnforceNumAndRealName("xiaobaoyuan", "F130005997", "肖宝元");
      // System.out.println(ui);
      // System.out.println(mapper.getClass().getSuperclass());
      // UserInfo blog = mapper.selectByNameAndEnforceNum("xiaobaoyuan","F130005997");
      // System.out.println(blog);

      // List<UserInfo> list = mapper.selectByIdInOrderByIdDescAndUserNameAsc(Arrays.asList(1, 2, 3));
//      List<UserInfo> list = mapper.selectByIdInAndPositionIdBetweenOrderByIdDescAndUserNameAsc(Arrays.asList(1, 2, 3),
//          1, 17);
//       List<UserInfo> list = mapper.selectByIdInAndPositionIdBetween(Arrays.asList(1, 2, 3), 1, 17);
//      List<UserInfo> list = mapper.findByOrgId(27);
//      System.out.println(list);
//      List<UserInfo> list2 = mapper.selectByIdIn(Arrays.asList(1, 2, 3));
//      System.out.println(list2);

      UserInfo userInfo = mapper.findById(1);
      System.out.println(userInfo);

      userInfo.setId(null);
      //新增使用
      mapper.insert(userInfo);
      System.out.println(userInfo);

      session.commit();

    }
  }
}
