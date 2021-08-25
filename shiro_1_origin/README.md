### 项目描述
- 本项目测试最基本的Shiro控制。
- 使用Shiro默认的session来管理权限。
- 使用Shiro默认的缓存来放session。
- 用本地Map存session，重启即失效。
- 本地Map意味着只能单机，无法多个服务共享session。
- 使用角色和资源权限两种方式。（注解的方式）
- 使用Shiro默认的过滤器。
- 使用Knife4j测试（Swagger的升级版）。
- 使用shiro-spring-boot-web-starter:1.7.0

### 角色的权限
- admin有所有权限
- productManager有所有product的权限
- orderManager有所有order的权限

### 用户密码等

| 用户        |    密码      |    角色          | 拥有的权限                                  |
| ---------- | ------------ | ---------------- | ---------------------------------- |
| zhang3     |  12345       | admin            |  所有权限                                     |
|  li4       | abcde        |productManager    | 产品的所有权限 |
