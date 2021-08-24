package com.example.demo.config.shiro;

import com.example.demo.common.constant.WhiteList;
import com.example.demo.config.shiro.filter.JwtFilter;
import com.example.demo.config.shiro.realm.AccountRealm;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
    // @Bean("shiroFilterFactoryBean")
    // public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
    //     ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    //     shiroFilterFactoryBean.setSecurityManager(securityManager);
    //     // 认证失败要跳转的地址。
    //     // shiroFilterFactoryBean.setLoginUrl("/login");
    //     // // 登录成功后要跳转的链接
    //     // shiroFilterFactoryBean.setSuccessUrl("/index");
    //     // // 未授权界面;
    //     // shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
    //
    //     Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
    //     filterChainDefinitionMap.put("/login", "anon");
    //
    //     WhiteList.ALL.forEach(str -> {
    //         filterChainDefinitionMap.put(str, "anon");
    //     });
    //
    //     // filterChainDefinitionMap.put("/logout", "logout");
    //     filterChainDefinitionMap.put("/**", "jwtAuthc");
    //
    //     Map<String, Filter> customisedFilters = new LinkedHashMap<>();
    //     // 不能用注入来设置过滤器。若用注入，则本过滤器优先级会最高（/**优先级最高，导致前边所有请求都无效）。
    //     // springboot会扫描所有实现了javax.servlet.Filter接口的类，无需加@Component也会扫描到。
    //     customisedFilters.put("jwtAuthc", new JwtFilter());
    //
    //     shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    //     shiroFilterFactoryBean.setFilters(customisedFilters);
    //
    //     return shiroFilterFactoryBean;
    // }
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

    // 名字必须是authc，用以替代shiro默认的authc。
    @Bean("authc")
    public AuthenticatingFilter authenticatingFilter() {
        return new JwtFilter();
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        // 关闭shiro自带的session。这样不能通过session登录shiro，后面将采用jwt凭证登录。
        // 见：http://shiro.apache.org/session-management.html#SessionManagement-DisablingSubjectStateSessionStorage
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getDatabaseRealm());
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public AccountRealm getDatabaseRealm() {
        return new AccountRealm();
    }

    /**
     * 支持shiro 注解。
     * 也可以在pom.xml里引入此依赖：org.springframework.boot:spring-boot-starter-aop
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
