package com.plus3.privilege.configuration;

import com.plus3.privilege.test.ITest;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * Created by admin on 2017/12/15.
 */
@Configuration
public class TestConfiguration {

    @Bean
    public ITest createITest() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ITest.class);
        enhancer.setCallback(new MethodInterceptor() {

            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("From ITest proxy");

                if (obj.getClass().isInterface())
                    return null;

                Object result = methodProxy.invokeSuper(obj, args);
                return result;
            }
        });
        ITest obj = (ITest) enhancer.create();
        return obj;
    }
}
