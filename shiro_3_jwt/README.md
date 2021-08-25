### 项目描述
- 本项目使用jwt替代默认的authc作为认证方式，其他不变。
- 使用角色和资源权限两种方式。（注解的方式）
- 使用Knife4j测试接口（Swagger的升级版）。
- 不使用缓存。
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

### 代码执行流程
#### login
JwtFilter#onAccessDenied  //正常返回true  
&emsp; PathMatchingFilter         //去anon过滤器查找，在里边，则放行  
&emsp;&emsp; 自己的login接口
#### 需权限的接口
JwtFilter#onAccessDenied  //执行executeLogin(servletRequest, servletResponse);  
&emsp; JwtFilter#createToken  
&emsp;&emsp; AccountRealm＃doGetAuthenticationInfo  
&emsp;&emsp;&emsp; AccountRealm＃doGetAuthorizationInfo  
&emsp;&emsp;&emsp;&emsp; 自己的接口