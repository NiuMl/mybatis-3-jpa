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

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {

  /**
  *
  */
  private Long id;
  /**
  *
  */
  private String userName;
  /**
  *
  */
  private String realName;
  /**
  *
  */
  private String enforceNum;
  /**
  *
  */
  private String password;
  /**
  *
  */
  private String salt;
  /**
   * 职位id
   */
  private Long positionId;
  /**
   * 职位属性
   */
  private Integer positionAttr;
  /**
  *
  */
  private Long orgId;
  /**
   * 逗号分隔的模块id
   */
  private String extPermission;
  /**
  *
  */
  private Integer provinceId;
  /**
  *
  */
  private Integer cityId;
  /**
  *
  */
  private Integer countryId;
  /**
  *
  */
  private Integer townId;
  /**
   * 0：男：1：女
   */
  private Integer sex;
  /**
  *
  */
  private String mobile;
  /**
  *
  */
  private Integer yn;
  /**
  *
  */
  private Date createTime;
  /**
  *
  */
  private Date updateTime;
  /**
   * 部门id
   */
  private Long departmentId;
  /**
  *
  */
  private String importPhone;
  /**
  *
  */
  private String userNameSpell;
  /**
  *
  */
  private Integer displayOrder;
  /**
  *
  */
  private String positionName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getEnforceNum() {
    return enforceNum;
  }

  public void setEnforceNum(String enforceNum) {
    this.enforceNum = enforceNum;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public Integer getPositionAttr() {
    return positionAttr;
  }

  public void setPositionAttr(Integer positionAttr) {
    this.positionAttr = positionAttr;
  }

  public Long getOrgId() {
    return orgId;
  }

  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  public String getExtPermission() {
    return extPermission;
  }

  public void setExtPermission(String extPermission) {
    this.extPermission = extPermission;
  }

  public Integer getProvinceId() {
    return provinceId;
  }

  public void setProvinceId(Integer provinceId) {
    this.provinceId = provinceId;
  }

  public Integer getCityId() {
    return cityId;
  }

  public void setCityId(Integer cityId) {
    this.cityId = cityId;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public Integer getTownId() {
    return townId;
  }

  public void setTownId(Integer townId) {
    this.townId = townId;
  }

  public Integer getSex() {
    return sex;
  }

  public void setSex(Integer sex) {
    this.sex = sex;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Integer getYn() {
    return yn;
  }

  public void setYn(Integer yn) {
    this.yn = yn;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getImportPhone() {
    return importPhone;
  }

  public void setImportPhone(String importPhone) {
    this.importPhone = importPhone;
  }

  public String getUserNameSpell() {
    return userNameSpell;
  }

  public void setUserNameSpell(String userNameSpell) {
    this.userNameSpell = userNameSpell;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getPositionName() {
    return positionName;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }

  @Override
  public String toString() {
    return "UserInfo{" + "id=" + id + ", userName='" + userName + '\'' + ", realName='" + realName + '\''
        + ", enforceNum='" + enforceNum + '\'' + ", password='" + password + '\'' + ", salt='" + salt + '\''
        + ", positionId=" + positionId + ", positionAttr=" + positionAttr + ", orgId=" + orgId + ", extPermission='"
        + extPermission + '\'' + ", provinceId=" + provinceId + ", cityId=" + cityId + ", countryId=" + countryId
        + ", townId=" + townId + ", sex=" + sex + ", mobile='" + mobile + '\'' + ", yn=" + yn + ", createTime="
        + createTime + ", updateTime=" + updateTime + ", departmentId=" + departmentId + ", importPhone='" + importPhone
        + '\'' + ", userNameSpell='" + userNameSpell + '\'' + ", displayOrder=" + displayOrder + ", positionName='"
        + positionName + '\'' + '}';
  }
}
