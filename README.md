

## 简介

**langchain4j**+**springboot**的学习项目，基于java17，spring-boot:3.4.3，langchain4j-spring-boot:1.0.0-beta1

平台为阿里云百炼，语言模型qwen-max，嵌入模型使用text-embedding-v2

向量数据库：redis 7.4.2

相关文档：[官方文档langchain4j](https://docs.langchain4j.dev/get-started)

## 启动

1、**（必选）**从阿里云百炼获取到apikey，设置到系统环境变量API_KEY_DASH_SCOPE，也可将配置文件中的API_KEY_DASH_SCOPE替换为自己的apikey

2、**（必选）**将redis配置更改为自己的

3、（可选）天气api使用的是高德的，如需天气工具可从高德开发者平台可申请到apikey，设置到系统环境变量API_KEY_GAODE



## Features

0、日志（基于ChatModelListener，方便debug和学习）

1、对话记忆(基于内存)

2、FunctionCall，实现了一些简单的工具

3、知识库，可上传文件、url

4、RAG



## Todo

1、根据传入的文件添加额外信息

2、websearch

3、对话历史记录持久化



## 常见问题

Q：为什么不使用自动装配的spring-boot-start

A：目前自动装配代码不完善，比如千问chatModel不会自动装配ChatModelListener；另外，这个一个学习项目，使用Advanced或Naive的Api更有利于对框架的学习和理解



