package com.touchealth.platform.processengine.shiro;

import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;

/**
 * 自定义的授权器。<br>
 * 修改内容：
 * <ol>
 *     <li>同一个资源多个权限，认证关系改为或。如：<br>
 *     <code>/xxx/xx, perms[a,b]</code>。默认 /xxx/xx 资源访问需要 a,b权限都具备。这里改为具备a或b均可访问。
 *     </li>
 * </ol>
 * @author SY
 */
public class MyModularRealmAuthorizer extends ModularRealmAuthorizer {

    @Override
    public boolean isPermittedAll(PrincipalCollection principals, String... permissions) {
        assertRealmsConfigured();
        if (permissions != null && permissions.length > 0) {
            for (String perm : permissions) {
                if (isPermitted(principals, perm)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isPermittedAll(PrincipalCollection principals, Collection<Permission> permissions) {
        assertRealmsConfigured();
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                if (isPermitted(principals, permission)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

}
