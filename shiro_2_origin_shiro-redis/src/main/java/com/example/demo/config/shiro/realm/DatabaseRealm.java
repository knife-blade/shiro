package com.example.demo.config.shiro.realm;

import com.example.demo.business.rbac.permission.service.PermissionService;
import com.example.demo.business.rbac.role.service.RoleService;
import com.example.demo.business.rbac.user.entity.User;
import com.example.demo.business.rbac.user.service.UserService;
import com.example.demo.common.exception.BusinessException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Set;

public class DatabaseRealm extends AuthorizingRealm {
    @Lazy
    @Autowired
    private UserService userService;

    @Lazy
    @Autowired
    private RoleService roleService;

    @Lazy
    @Autowired
    private PermissionService permissionService;

    // 登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        // 获取账号密码
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        String userName = token.getPrincipal().toString();
        // 获取数据库中的密码
        User user = userService.lambdaQuery().eq(User::getUserName, userName).one();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        // 认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
        // 盐也放进去
        // 这样通过ShiroConfig里配置的 HashedCredentialsMatcher 进行自动校验
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userName, passwordInDB, ByteSource.Util.bytes(salt), getName());
        return authenticationInfo;
    }

    // 权限验证
    // 只有用到org.apache.shiro.web.filter.authz包里默认的过滤器才会走到这里。
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 能进入到这里，表示账号已经通过认证了
        String userName = (String) principalCollection.getPrimaryPrincipal();
        // 通过service获取角色和权限
        Set<String> permissions = permissionService.getPermissionsByUserName(userName);
        Set<String> roles = roleService.getRolesByUserName(userName);

        // 授权对象
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        // 把通过service获取到的角色和权限放进去
        s.setStringPermissions(permissions);
        s.setRoles(roles);
        return s;
    }
}
