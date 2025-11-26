# 学生作业订正系统

这是一个基于Spring Boot 3、Vue 2和Element UI的学生作业订正系统，专为教师设计用于跟踪和管理学生的作业订正情况。

## 系统功能

1. 显示醒目的标题"五4班作业订正系统"
2. 实时显示当前时间，精确到秒
3. 日期选择器可查看过去某天的订正数据
4. 前一天、后一天按钮方便切换日期
5. 可输入并保存特定日期和作业类型的作业内容
6. 按学号排列的10个学生按钮组成的网格
7. 学生按钮初始为红色，点击变为绿色表示已订正，再次点击恢复红色
8. 支持5种作业本类型：听写本、生字本、词语本、综合本、家默本
9. 不同作业类型通过按钮切换展示
10. 重置当前日期状态功能，带确认弹窗
11. 显示已订正和未订正数量统计
12. 学生搜索功能，可按学号、姓名或拼音首字母搜索
13. 查看学生历史欠交作业记录功能
14. 查看历史欠交学生名单功能
15. 自动排除无学生交作业的日期（视为放假）

## 技术栈

- 后端：Spring Boot 3
- 前端：Vue 2 + Element UI
- 数据库：MySQL

## 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS correction_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行 [database/schema.sql](database/schema.sql) 文件中的SQL语句创建表和初始化数据

## 项目依赖说明

项目已包含以下主要依赖：

- spring-boot-starter-web：Web应用支持
- mybatis-spring-boot-starter：MyBatis支持
- mysql-connector-j：MySQL驱动
- lombok：简化Java代码工具

## 如何运行

1. 确保已安装JDK 17+和Maven
2. 配置MySQL数据库连接信息（在[application.properties](src/main/resources/application.properties)中）
3. 创建数据库并执行建表脚本
4. 使用Maven构建并运行项目：
   ```
   mvn clean install
   mvn spring-boot:run
   ```
5. 访问 http://localhost:8080 查看应用

## API接口

### 学生相关
- GET `/api/students` - 获取所有学生
- GET `/api/students/{id}` - 根据ID获取学生
- GET `/api/students/search?keyword={keyword}` - 搜索学生

### 作业类型相关
- GET `/api/homework-types` - 获取所有作业类型

### 作业内容相关
- GET `/api/homework-contents?date={date}&typeId={typeId}` - 获取指定日期和类型的作业内容
- POST `/api/homework-contents` - 保存作业内容

### 订正记录相关
- GET `/api/correction-records?date={date}&typeId={typeId}` - 获取指定日期和类型的订正记录
- POST `/api/correction-records` - 保存订正记录
- PUT `/api/correction-records/reset?date={date}&typeId={typeId}` - 重置指定日期和类型的订正状态
- GET `/api/correction-records/statistics?date={date}&typeId={typeId}` - 获取统计数据
- GET `/api/correction-records/unfinished-students?typeId={typeId}` - 获取未完成订正的学生列表
- GET `/api/correction-records/student-unfinished?studentId={studentId}&typeId={typeId}` - 获取指定学生未完成的订正记录

## 前端页面

前端页面位于 [src/main/resources/static/index.html](src/main/resources/static/index.html)，包含了所有用户界面和交互逻辑。

## 注意事项

1. 系统默认将数据存储在MySQL数据库中
2. 前端使用了Element UI组件库和Vue 2框架
3. 所有API接口均支持跨域请求