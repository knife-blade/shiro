### 项目描述
本项目测试最基本的Shiro控制。
- 使用Shiro的默认的session控制。
- 全部使用注解控制。
- 有角色、资源权限两种方式。

### 角色的权限
- admin有所有权限
- productManager有所有product的权限
- orderManager有所有order的权限

### 用户密码等

| 用户        |    密码      |    角色          | 拥有的权限                                  |
| ---------- | ------------ | ---------------- | ---------------------------------- |
| zhang3     |  12345       | admin            |  所有权限                                     |
|  li4       | abcde        |productManager    | 产品的所有权限 |
