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

/***
 * @author niumengliang Date:2023/12/20 Time:15:58
 */
public class JpaXml {

  public static final String base = """
      <?xml version="1.0" encoding="UTF-8" ?>
          <!DOCTYPE mapper
                  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                  "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
          <mapper namespace="{namespace}">
              {method}
          </mapper>
      """;
  public static final String select = """
      <select id="{id}" resultType="{resultType}">
          {sql}
      </select>
      """;

  public static String assembleSql(String id, String resultType, String sql, String namespace) {
    return base.replace("{namespace}", namespace).replace("{method}",
        select.replace("{id}", id).replace("{resultType}", resultType).replace("{sql}", sql));
  }
}
