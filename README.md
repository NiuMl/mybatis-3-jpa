

- 基于 Mybatis 3.5.16-SNAPSHOT进行增强，实现类似于spring data jpa功能
- Enhanced based on Mybatis 3.5.16-SNAPSHOT to achieve functionality similar to spring data jpa


测试包：com.niuml.RunTest
打包用 master-clean分支，没有测试类，干净点


## 更新日志
还是加个日志吧，不然好low
* by 2024 11 27 增加"新增"操作,这个方法不会解析主动操作后面的语句，比如 xxx.insertXXXByXXX,只解析insert和该方法的第一个入参，其它不解析<br/>
    使用方式如 xxx.insert()、save、add (Bean) <br/>
    也可以增加@Param("XXX"),这个注解是mybatis自带的,用了这个，生成的sql会对字段加个别名
* by 2024 11 28 增加批量新增的操作，只要是新增的语法，传入集合就是批量，单个就是单个新增
* by 2024 11 29 增加更新单个操作





MyBatis SQL Mapper Framework for Java
=====================================

[![build](https://github.com/mybatis/mybatis-3/workflows/Java%20CI/badge.svg)](https://github.com/mybatis/mybatis-3/actions?query=workflow%3A%22Java+CI%22)
[![Coverage Status](https://coveralls.io/repos/mybatis/mybatis-3/badge.svg?branch=master&service=github)](https://coveralls.io/github/mybatis/mybatis-3?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/org.mybatis/mybatis.svg)](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis/)
[![License](https://img.shields.io/:license-apache-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Stack Overflow](https://img.shields.io/:stack%20overflow-mybatis-brightgreen.svg)](https://stackoverflow.com/questions/tagged/mybatis)
[![Project Stats](https://www.openhub.net/p/mybatis/widgets/project_thin_badge.gif)](https://www.openhub.net/p/mybatis)

![mybatis](https://mybatis.org/images/mybatis-logo.png)

The MyBatis SQL mapper framework makes it easier to use a relational database with object-oriented applications.
MyBatis couples objects with stored procedures or SQL statements using an XML descriptor or annotations.
Simplicity is the biggest advantage of the MyBatis data mapper over object relational mapping tools.

Essentials
----------

* [See the docs](https://mybatis.org/mybatis-3)
* [Download Latest](https://github.com/mybatis/mybatis-3/releases)
* [Download Snapshot](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis/)

Contributions
-------------

Mybatis-core is now being auto formatted.  Given nature of some code logic with mybatis, it is more appropriate to force a formatting structure manually for snippets such as sql statements.  To do so, add following blocks around code.

- ```// @formatter:off``` to start the block of unformatted code
- ```// @formatter:on``` to end the block of unformatted code

If comment sections need same behaviour such as javadocs, note that the entire block must be around entire comment as direct usage does not properly indicate that formatter treats it all as one comment block regardless.

Tests
-----

Mybatis-3 code runs more expressive testing depending on jdk usage and platform.

By default, we set ```<excludedGroups>TestcontainersTests</excludedGroups>``` which will exclude a subset of tests with @Tag('TestcontainersTests').  Further, if pre jdk 16, we will further exclude record classes from executions further reducing tests.

When using jdk 16+, we adjust the rule to ```<excludedGroups>TestcontainersTests,RequireIllegalAccess</excludedGroups>```.

When we run on ci platform, we further make adjustments as needed.  See [here](.github/workflows/ci.yaml) for details.

As of 2/20/2023, using combined system + jdk will result in given number of tests ran.  This will change as tests are added or removed over time.

without adjusting settings (ie use as is, platform does not matter)

- any OS + jdk 11 = 1730 tests
- any OS + jdk 17 = 1710 tests
- any OS + jdk 19 = 1710 tests
- any OS + jdk 20 = 1710 tests
- any OS + jdk 21 = 1710 tests

our adjustments for GH actions where platform does matter

- windows + jdk 11 = 1730 tests
- windows + jdk 17 = 1710 tests
- windows + jdk 19 = 1710 tests
- windows + jdk 20 = 1710 tests
- windows + jdk 21 = 1710 tests

- linux + jdk 11 = 1765 tests
- linux + jdk 17 = 1745 tests
- linux + jdk 19 = 1745 tests
- linux + jdk 20 = 1745 tests
- linux + jdk 21 = 1745 tests

- mac + jdk 11 = 1730 tests
- mac + jdk 17 = 1710 tests
- mac + jdk 19 = 1710 tests
- mac + jdk 20 = 1710 tests
- mac + jdk 21 = 1710 tests
