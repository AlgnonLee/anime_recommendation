package org.anime.interceptor;


import org.anime.service.redisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private redisService redisService;

    public void addInterceptors(InterceptorRegistry registry){
        InterceptorRegistration registration = registry.addInterceptor(new MyInterceptor(redisService));
        registration.addPathPatterns("/view/recommendation","/view/chat","/view/anime/**","/view/userInfo");//拦截所有路径,/**表示匹配多层地址
    }

}
