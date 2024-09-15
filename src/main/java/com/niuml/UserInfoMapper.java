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

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.jpa.JpaTable;

@JpaTable("user_info")
@Mapper
public interface UserInfoMapper {

  // @Select("select * from hbzf_user.hfd_user_info where user_name = #{s} and enforce_num = #{s1}")
  UserInfo selectByNameAndEnforceNum(@Param("s") String s, @Param("s1") String s1);

  List<UserInfo> selectListByCityId(int cityId);

  UserInfo selectByNameAndEnforceNum2(@Param("s") String s, @Param("s1") String s1);

  UserInfo selectByUserNameAndEnforceNumAndRealName(String userName, String enforceNum, String realName);

  List<UserInfo> selectByIdIn(List<Integer> list);

  List<UserInfo> selectByIdInOrderById(List<Integer> list);

  List<UserInfo> selectByIdInOrderByIdAsc(List<Integer> list);

  List<UserInfo> selectByIdInOrderByIdDesc(List<Integer> list);

  List<UserInfo> selectByIdInOrderByIdDescAndUserNameAsc(List<Integer> list);

  List<UserInfo> selectByIdInAndPositionIdBetweenOrderByIdDescAndUserNameAsc(List<Integer> list, int pi1, int pi2);

  List<UserInfo> selectByIdInAndPositionIdBetween(List<Integer> list, int i, int i1);

  List<UserInfo> findByOrgId(int i);
}
