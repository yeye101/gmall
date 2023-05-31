# mall

#### 介绍
分布式电子商城
在Guli的基础上增加了`商品上下架功能`、`后台库存补充功能`、`用户详情资料页面`、`订单详情页面`
<br>可以随意使用，毕设什么的都可以


#### 软件架构
基于 SpringCloud + SpringCloudAlibaba + MyBatis-Plus实现，采用 Docker 容器化部署。
```
mall
├── mall-common -- 工具类及通用代码
├── renren-generator -- 人人开源项目的代码生成器
├── mall-auth-server -- 认证中心（社交登录、OAuth2.0、单点登录）
├── mall-cart -- 购物车服务
├── mall-coupon -- 优惠卷服务
├── mall-gateway -- 统一配置网关
├── mall-order -- 订单服务
├── mall-product -- 商品服务
├── mall-search -- 检索服务
├── mall-seckill -- 秒杀服务
├── mall-third-party -- 第三方服务
├── mall-ware -- 仓储服务
└── mall-member -- 会员服务
```

#### 安装教程

1. 检查maven仓库和变量配置，不然会报错
2. 启动时必须先编译common模块，否则无法识别公共模块的变量
3. 报错时必须先清缓存，再重新编译，启动

> Windows环境部署

- 修改本机的host文件，映射域名端口
```
127.0.0.1 my-yeye.com
127.0.0.1 mall-yeye.com
127.0.0.1 search.mall-yeye.com
127.0.0.1 item.mall-yeye.com
127.0.0.1 auth.mall-yeye.com
127.0.0.1 cart.mall-yeye.com
127.0.0.1 order.mall-yeye.com
127.0.0.1 member.mall-yeye.com
127.0.0.1 seckill.mall-yeye.com
```

#### 使用说明
1. 通过git下载源码
2. idea、eclipse需安装lombok插件，不然会提示找不到entity的get set方法
3. 执行db/**.sql文件，初始化数据
4. 执行docker的部署文件在docker.txt中有写
5. 在es中执行商品的es索引（在start.txt中）
6. 修改Linux或者本地的Nginx的配置文件（在start.txt中nginx.config和mall.config）
7. 拉取nginx的静态文件，文件项目地址：https://gitee.com/yeye001/www
8. 修改application.yml和bootstrap.yml文件，更新nacos、MySQL、redis、es、rabbitmq的账号和密码，注意需要修改的地方已经使用todo标记，可能会有遗漏，如有发现各位可以自行修改，也可以提交pr或者is，帮助我完善项目。
9. 在最后启动前端项目，前端地址：https://gitee.com/yeye001/vue
10. 账号密码：admin/admin
11. xxx--等待补充

#### 开发工具

|     工具      |        说明         |                      官网                       |
| :-----------: | :-----------------: | :---------------------------------------------: |
|     IDEA      |    开发Java程序     |     https://www.jetbrains.com/idea/download     |
| RedisDesktop  | redis客户端连接工具 |        https://redisdesktop.com/download        |
|  SwitchHosts  |    本地host管理     |       https://oldj.github.io/SwitchHosts        |
|    X-shell    |  Linux远程连接工具  | http://www.netsarang.com/download/software.html |
|    Navicat    |   数据库连接工具    |       http://www.formysql.com/xiazai.html       |
| PowerDesigner |   数据库设计工具    |             http://powerdesigner.de             |
|    Postman    |   API接口调试工具   |             https://www.postman.com             |
|    Jmeter     |    性能压测工具     |            https://jmeter.apache.org            |
|    Typora     |   Markdown编辑器    |                https://typora.io                |

#### 开发环境

|     工具      | 版本号 |                             下载                             |
| :-----------: | :----: | :----------------------------------------------------------: |
|      JDK      |  1.8   | https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html |
|     Mysql     |  5.8   |                    https://www.mysql.com                     |
|     Redis     | Redis  |                  https://redis.io/download                   |
| Elasticsearch | 7.6.2  |               https://www.elastic.co/downloads               |
|    Kibana     | 7.6.2  |               https://www.elastic.co/cn/kibana               |
|   RabbitMQ    | 3.8.5  |            http://www.rabbitmq.com/download.html             |
|     Nginx     | 1.1.6  |              http://nginx.org/en/download.html               |

注意：以上的除了jdk都是采用docker方式进行安装，详细安装步骤可参考百度!!!
注意：执行docker的部署文件在docker.txt中有写，不一定全有

- 克隆前端项目 `vue` 以 `npm run dev` 方式去运行
- 克隆整个后端项目 `mall` ，并导入 IDEA 中完成编译




#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 其他
如果觉得对你有用或者有帮助的话点个star吧，有问题可以在issues提交。

