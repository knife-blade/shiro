package com.example.demo.config.shiro;

import com.example.demo.common.constant.WhiteList;
import com.example.demo.config.shiro.filter.JwtFilter;
import com.example.demo.config.shiro.filter.UrlFilter;
import com.example.demo.config.shiro.realm.AccountRealm;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Lazy
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果实现了AuthenticatingFilter.java要设置下边这个，因为shiro很多地方依据loginUrl进行判断
        // shiroFilterFactoryBean.setLoginUrl("/login");
        // // 登录成功后要跳转的链接
        // shiroFilterFactoryBean.setSuccessUrl("/index");
        // // 未授权界面;
        // shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        WhiteList.ALL.forEach(str -> {
            filterChainDefinitionMap.put(str, "anon");
        });

        filterChainDefinitionMap.put("/login", "anon");
        // 下边这行不要打开。原因待确定
        // filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/**", "jwtAuthc,urlAuthz");

        Map<String, Filter> customisedFilters = new LinkedHashMap<>();
        //不能通过注入来设置过滤器。如果通过注入，则本过滤器优先级会最高（/**优先级最高，导致前边所有请求都无效）。
        // springboot会扫描所有实现了javax.servlet.Filter接口的类，无需加@Component也会扫描到。
        customisedFilters.put("jwtAuthc", new JwtFilter());
        customisedFilters.put("urlAuthz", new UrlFilter());

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        shiroFilterFactoryBean.setFilters(customisedFilters);

        return shiroFilterFactoryBean;
    }
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

    /**
     * 此种配置方法在本项目中跑不通。
     */
    /* @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 认证失败要跳转的地址。
        // shiroFilterFactoryBean.setLoginUrl("/login");
        // // 登录成功后要跳转的链接
        // shiroFilterFactoryBean.setSuccessUrl("/index");
        // // 未授权界面;
        // shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/login", "anon");

        WhiteList.ALL.forEach(str -> {
            filterChainDefinitionMap.put(str, "anon");
        });

        // filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/**", "jwtAuthc");

        Map<String, Filter> customisedFilters = new LinkedHashMap<>();
        // 不能用注入来设置过滤器。若用注入，则本过滤器优先级会最高（/**优先级最高，导致前边所有请求都无效）。
        // springboot会扫描所有实现了javax.servlet.Filter接口的类，无需加@Component也会扫描到。
        customisedFilters.put("jwtAuthc", new JwtFilter());

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        shiroFilterFactoryBean.setFilters(customisedFilters);

        return shiroFilterFactoryBean;
    }*/
}
