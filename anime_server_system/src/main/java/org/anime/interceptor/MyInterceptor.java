package org.anime.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.anime.service.redisService;

import java.io.IOException;

public class MyInterceptor implements HandlerInterceptor {

    private redisService redisService;

    public MyInterceptor(redisService redisService) {
        this.redisService = redisService;
    }
    /**
     * preHandle()方法会在控制器类中的方法被执行之前,对请求进行处理时被执行
     * @param request   请求对象
     * @param response  响应对象
     * @param handler   请求的处理程序对象
     */

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws IOException {
        if(handler instanceof HandlerMethod){//如果是HandlerMethod对象
            HandlerMethod method=(HandlerMethod) handler;
            String name = null;
            String key = null;
            if(request.getCookies()!=null){
                for (Cookie cookie : request.getCookies()) {
                    if(cookie.getName().equals("login_name")){
                        name=cookie.getValue();
                    }
                    if(cookie.getName().equals("login_key")){
                        key=cookie.getValue();
                    }
                }
                if(name == null){
                    response.sendRedirect("/view/login");
                }else if(redisService.get(name).equals(key)){
                    return true;
                }else response.sendRedirect("/view/login");
            }else response.sendRedirect("/view/login");
        }
        return false;
    }
    //postHandle()方法会在控制器类中的方法被执行之后,对请求进行处理时被执行
    public void postHandle(HttpServletRequest request, HttpServletResponse response,Object handler,
                           @Nullable ModelAndView modelAndView){

    }
    //afterCompletion()方法会在整个请求结束之后被执行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,Object handler,
                                @Nullable  Exception ex){

    }
}
