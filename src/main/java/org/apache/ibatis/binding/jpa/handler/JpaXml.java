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
package org.apache.ibatis.binding.jpa.handler;

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

  public static final String insert = """
    <insert id="{id}" keyProperty="id" useGeneratedKeys="true">
        {sql}
    </insert>
    """;
  public static final String insertBatch = """
    <insert id="{id}" keyProperty="id" useGeneratedKeys="true">
      insert into {tableName}({fields})values
      <foreach collection="{entities}" item="entity" separator=",">
        ({sql})
      </foreach>
    </insert>
    """;
  public static final String update = """
    TODO
    """;
  public static final String delete = """
    TODO
    """;

  //组装查询使用sql语句
  public static String assembleSelectSql(String id, String resultType, String sql, String namespace) {
    return base.replace("{namespace}", namespace).replace("{method}",
      select.replace("{id}", id).replace("{resultType}", resultType).replace("{sql}", sql));
  }

  //组装插入使用sql语句
  public static String assembleInsetSql(String id, String sql, String namespace) {
    return base.replace("{namespace}", namespace).replace("{method}",
      insert.replace("{id}", id).replace("{sql}", sql));
  }

  public static String assembleInsetBatchSql(String id, String sql, String namespace, String entities, String tableName,String fields) {
    return base.replace("{namespace}", namespace)
      .replace("{method}",
        insertBatch.replace("{tableName}", tableName)
          .replace("{entities}", entities)
          .replace("{fields}", fields)
          .replace("{id}", id).replace("{sql}", sql));
  }
}
