 package com.niuml;


 import org.apache.ibatis.annotations.Mapper;
 import org.apache.ibatis.annotations.Param;
 import org.apache.ibatis.binding.jpa.JpaTable;

 import java.util.List;

 @JpaTable("hbzf_user.hfd_user_info")
 @Mapper
 public interface UserInfoMapper  {

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
   List<UserInfo> selectByIdInAndPositionIdBetweenOrderByIdDescAndUserNameAsc(List<Integer> list,int pi1,int pi2);
 }
