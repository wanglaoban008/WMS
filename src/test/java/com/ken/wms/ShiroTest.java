package com.ken.wms;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by za-xuda on 2017/8/17.
 */
class ShiroTest {

    @Test
    public void test() {
        Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("D:\\workspace\\wms\\WMS\\src\\test\\java\\auth.ini");
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        org.apache.shiro.subject.Subject user = SecurityUtils.getSubject();
        System.out.println("User is authenticated :" + user.isAuthenticated());
        System.out.println("====");
        UsernamePasswordToken token = new UsernamePasswordToken("xuda", "123");
        user.login(token);
        System.out.println("User is authenticated :" + user.isAuthenticated());
        System.out.println("====");
    }
}
