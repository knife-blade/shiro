### 测试结果
测试未通过。原因如下：
- shiro-redis在写缓存的时候要序列化对象，对象必须实现Serializable
- login接口里边要获取Subject，它获取到的是：WebDelegatingSubject。
- WebDelegatingSubject没有实现Serializable接口
- 所以调登录接口时会报错：org.apache.shiro.session.UnknownSessionException: org.crazycake.shiro.exception.SerializationException: serialize error, object=org.apache.shiro.session.mgt.SimpleSession,id=e78f793d-cc6e-47c7-976f-4392283f6b41

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
