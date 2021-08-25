package com.example.demo.config.shiro;

import com.example.demo.common.constant.WhiteList;
import com.example.demo.config.shiro.realm.DatabaseRealm;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        chainDefinition.addPathDefinition("/login", "anon");

        WhiteList.ALL.forEach(str -> {
            chainDefinition.addPathDefinition(str, "anon");
        });

        // all other paths require a logged in user
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }

    // 名字必须是securityManager
    @Bean(name = "securityManager")
    public SessionsSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(getDatabaseRealm());
        securityManager.setSessionManager(sessionManager());
        // securityManager.setCacheManager();  //支持缓存
        return securityManager;
    }

    // 名字必须是sessionManager
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置session过期时间（5分钟）。单位：ms，默认为30分钟。
        sessionManager.setGlobalSessionTimeout(300000L);
        return sessionManager;
    }

    @Bean
    public DatabaseRealm getDatabaseRealm() {
        DatabaseRealm myShiroRealm = new DatabaseRealm();
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 凭证匹配器。密码校验交给Shiro的SimpleAuthenticationInfo进行处理。
     *  对应：DatabaseRealm#doGetAuthenticationInfo(AuthenticationToken)
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5("xxx"));
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);

        return hashedCredentialsMatcher;
    }

    /**
     * setUsePrefix(true)用于解决一个奇怪的bug。如下：
     *  在引入spring aop的情况下，在@Controller注解的类的方法中加入@RequiresRole等
     *  shiro注解，会导致该方法无法映射请求，导致返回404。加入这项配置能解决这个bug。
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 开启shiro 注解。比如：@RequiresRole
     * 本处不用此方法开启注解，使用引入spring aop依赖的方式。原因见：application.yml里的注释
     */
    /*@Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }*/
}
