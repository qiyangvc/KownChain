# MyPersonalKnowledgeBase

## 个人知识库构建网站

### 目前设想：

1. 个人笔记功能
2. 笔记/知识库关联功能
3. （待补充）



# 后端

## 后端项目结构说明

```
KnowChain/
|
├──src/
|   |
|   ├──main/
|   |   |
|   |   └──java/
|   |       |
|   |       ├──resources/
|   |       |   |
|   |       |   ├── mapper/ 存放mybatis映射 .xml 格式文件
|   |       |   |
|   |       |   └──application.yml
|   |       |
|   |       └──knowchain/
|   |           |
|   |           ├──common/ 存放通用类，包括返回结果、异常类、枚举类等
|   |           |
|   |           ├──pojo/ 存放实体对象、数据传输对象（DTO）、视图对象（VO）这三种类
|   |           |   |
|   |           |   ├── DTO/ 接受前端数据格式
|   |           |   |
|   |           |   ├── VO/ 返回前端数据格式
|   |           |   |
|   |           |   └── entity/ 实体对象，和数据库表的字段一一对应
|   |           |   
|   |           └──server/ 后端功能实现
|   |               |
|   |               ├──controller/  控制层
|   |               |
|   |               ├──service/ 业务层
|   |               |
|   |               ├──mapper/  数据层
|   |               |
|   |               └──KnowChainApplication.java 应用启动
|   |
|   └──test/
|
└──pom.xml  依赖包配置文件

```