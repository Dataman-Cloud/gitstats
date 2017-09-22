package com.dataman.gitstats.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Created by biancl on 2017-09-21.
 */
@Component
public class LogHandlerInterceptor extends HandlerInterceptorAdapter{

    Logger logger= LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("请求路径：{},请求参数：{}",request.getRequestURI(),request.getParameterMap().toString());
        return true;
    }
}
