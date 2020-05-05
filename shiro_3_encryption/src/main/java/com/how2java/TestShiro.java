package com.how2java;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class TestShiro {
    public static void main(String[] args) {
    	//这里要释放注释，先注册一个用户
    	new DAO().createUser("tom", "123");
        
        User user = new User();
        user.setName("tom");
        user.setPassword("123");
        if(login(user))
        	System.out.println("Succeed");
        else
        	System.out.println("Fail");
        
    }
    
	private static boolean hasRole(User user, String role) {
		Subject subject = getSubject(user);
		return subject.hasRole(role);
	}
	
	private static boolean isPermitted(User user, String permit) {
		Subject subject = getSubject(user);
		return subject.isPermitted(permit);
	}

	private static Subject getSubject(User user) {
		//加载配置文件，并获取工厂
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		//获取安全管理者实例
		SecurityManager sm = factory.getInstance();
		//将安全管理者放入全局对象
		SecurityUtils.setSecurityManager(sm);
		//全局对象通过安全管理者生成Subject对象
		Subject subject = SecurityUtils.getSubject();
		
		return subject;
	}
	
	
	private static boolean login(User user) {
		Subject subject= getSubject(user);
		//如果已经登录过了，退出
		if(subject.isAuthenticated())
			subject.logout();
		//封装用户的数据
		UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
		try {
			//将用户的数据token 最终传递到Realm中进行对比
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//验证错误
			return false;
		}

		return subject.isAuthenticated();
	}
    
    
    
    
}
