

## 简介

**langchain4j**+**springboot**的学习项目，基于java17，spring-boot:3.4.3，langchain4j-spring-boot:1.0.0-beta1

平台为阿里云百炼，语言模型qwen-max，嵌入模型使用text-embedding-v2

向量数据库：redis 7.4.2

相关文档：[官方文档langchain4j](https://docs.langchain4j.dev/get-started)

## 启动

1、**（必选）**从阿里云百炼获取到apikey，设置到系统环境变量API_KEY_DASH_SCOPE，也可将配置文件中的API_KEY_DASH_SCOPE替换为自己的apikey

2、**（必选）**将redis配置更改为自己的

3、（可选）天气api使用的是高德的，如需天气工具可从高德开发者平台可申请到apikey，设置到系统环境变量API_KEY_GAODE

4、（可选）需要联网搜索功能时，需要安装[searxng](https://docs.searxng.org/index.html)，国内搜索引擎支持较差。用它的主要原因是免费。[docker部署](https://docs.searxng.org/admin/installation-docker.html)，以下为部署命令，部署完成后修改application.yaml中searxng下的相关配置

```
docker pull searxng/searxng
docker run --rm  -p 宿主机端口:8080 -v "持久卷:/etc/searxng"  -e "BASE_URL=http://宿主机Ip:宿主机端口/" -e "INSTANCE_NAME=my-instance" --name searxng -d  searxng/searxng
```



## 功能

0、日志（基于ChatModelListener，方便debug和学习）

1、对话记忆(基于内存)

2、FunctionCall，实现了一些简单的工具

3、知识库，可上传文件、url，支持的文件格式见[Tika](https://tika.apache.org/2.9.1/formats.html)

4、EmbeddingStoreRAG

5、联网搜索websearch，支持开关



## Todo

1、提示器Prompt管理

2、根据传入的文件添加额外信息

3、对话历史记录持久化

4、GraphRag，知识图谱，结合大模型实现文档到图数据库

5、多模态支持

6、接入更多的ai平台，ollama，deepseek...



## 常见问题

Q：为什么不使用自动装配的spring-boot-start

A：目前自动装配代码不完善，比如千问chatModel不会自动装配ChatModelListener；另外，这个一个学习项目，使用Advanced或Naive的Api更有利于对框架的学习和理解



