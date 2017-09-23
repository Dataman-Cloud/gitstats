package com.dataman.gitstats.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by biancl on 2017-09-21.
 */
@Component
public class ConsumeTimeWatchHandlerInterceptor extends HandlerInterceptorAdapter {

    Logger logger= LoggerFactory.getLogger(this.getClass());
    private NamedThreadLocal<Long> startTimeThreadLocal=new NamedThreadLocal<Long>("SpentTimeWatch-StartTime");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTimeThreadLocal.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long beginTime=startTimeThreadLocal.get();
        long endTime=System.currentTimeMillis();
        long consumeTime=endTime-beginTime;
        logger.debug("{}花费时间：{}",request.getRequestURI(),consumeTime);
    }
}
