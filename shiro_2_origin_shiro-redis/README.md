### 测试结果
测试通过。
### 项目描述
- 本项目测试最基本的Shiro控制 使用shiro-redis这个第三方redis缓存。
- 使用Shiro默认的session来管理权限。
- 使用角色和资源权限两种方式。（注解的方式）
- 使用Shiro默认的过滤器。
- 使用Knife4j测试接口（Swagger的升级版）。

### 角色的权限
- admin有所有权限
- productManager有所有product的权限
- orderManager有所有order的权限

### 用户密码等

| 用户        |    密码      |    角色          | 拥有的权限                                  |
| ---------- | ------------ | ---------------- | ---------------------------------- |
| zhang3     |  12345       | admin            |  所有权限                                     |
|  li4       | abcde        |productManager    | 产品的所有权限 |
