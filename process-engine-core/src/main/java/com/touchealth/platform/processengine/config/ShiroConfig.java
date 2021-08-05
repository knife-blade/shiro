package com.touchealth.platform.processengine.config;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.entity.user.ResourcePerms;
import com.touchealth.platform.processengine.service.user.ResourcePermsService;
import com.touchealth.platform.processengine.shiro.CustomFormAuthenticationFilter;
import com.touchealth.platform.processengine.shiro.CustomPermissionsAuthorizationFilter;
import com.touchealth.platform.processengine.shiro.CustomRealm;
import com.touchealth.platform.processengine.shiro.MyModularRealmAuthorizer;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.*;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.touchealth.platform.processengine.constant.AuthConstant.PERMS_WHITELIST;
import static com.touchealth.platform.processengine.constant.PermsConstant.*;

/**
 * Shiro配置<br>
 * <font color=red>注意不要导入ShiroAnnotationProcessorConfiguration配置,会出现循环依赖注入问题.
 * 并且目前无法支持注解方式,会导致@Cacheable注解失去作用.</font>
 * @author SunYang
 */
@Configuration
@Import({ShiroBeanConfiguration.class,
//        ShiroAnnotationProcessorConfiguration.class,
        ShiroWebFilterConfiguration.class,
        ShiroRequestMappingConfig.class})
public class ShiroConfig extends ShiroWebConfiguration {

    @Value("#{@environment['shiro.pass'] ?: false}")
    private Boolean shiroPass;
    @Value("${spring.profiles.active}")
    private String activeEnv;

    @Resource
    private ResourcePermsService resourcePermsService;

    @Bean
    @Override
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        if (!CommonConstant.PRO_ENV.equals(activeEnv) && shiroPass != null && shiroPass) {
            chainDefinition.addPathDefinition("/**", "anon");
            return chainDefinition;
        }

        // 无需授权资源
        Arrays.stream(PERMS_WHITELIST).forEach(passPerm -> {
            chainDefinition.addPathDefinition(passPerm, "anon");
        });
        // 移动端无需授权
        chainDefinition.addPathDefinition("/mobile/**", "anon");

        List<ResourcePerms> resourcePerms = resourcePermsService.listAll(CommonConstant.APP_NAME);

        // 设置资源访问权限
        if (!CollectionUtils.isEmpty(resourcePerms)) {
            resourcePerms.forEach(rp ->
                    chainDefinition.addPathDefinition(rp.getResource(), "authc, perms[" + rp.getPerms() + "]"));
        }

        // 其他资源，需要授权
        chainDefinition.addPathDefinition("/**", "authc");

        return chainDefinition;
    }

    @Bean
    public Realm realm() {
        return new CustomRealm();
    }

    /**
     * 自定义认证过滤器，<font color=red>不要改方法名字.</font>
     * @return
     */
    @Bean
    public FormAuthenticationFilter authc() {
        return new CustomFormAuthenticationFilter();
    }

    /**
     * 自定义权限过滤器，<font color=red>不要改方法名字.</font>
     * @return
     */
    @Bean
    public PermissionsAuthorizationFilter perms() {
        return new CustomPermissionsAuthorizationFilter();
    }

    @Override
    protected Authorizer authorizer() {
        ModularRealmAuthorizer authorizer = new MyModularRealmAuthorizer();
        if (permissionResolver != null) {
            authorizer.setPermissionResolver(permissionResolver);
        }
        if (rolePermissionResolver != null) {
            authorizer.setRolePermissionResolver(rolePermissionResolver);
        }
        return authorizer;
    }

    //    /**
//     * 支持注解权限配置
//     * @param securityManager
//     * @return
//     * TODO 添加AuthorizationAttributeSourceAdvisor后,会导致@Cacheable注解失效,无法缓存,具体原因待分析
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
//        advisor.setSecurityManager(securityManager);
//        return advisor;
//    }

}
