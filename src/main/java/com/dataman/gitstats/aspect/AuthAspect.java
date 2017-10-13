package com.dataman.gitstats.aspect;

import com.dataman.gitstats.exception.ApiResultCode;
import com.dataman.gitstats.exception.BusinessException;
import com.dataman.gitstats.po.User;
import com.dataman.gitstats.service.LoginService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 权限认证
 * @author biancl
 * @date 2017-10-12 12:04
 * @Copyright © 2017北京数人科技有限公司
 */
@Aspect
@Component
public class AuthAspect {

    private static Logger log=LoggerFactory.getLogger(AuthAspect.class);

    @Autowired
    private LoginService loginService;

    /**
     * @method: authAspect 定义注解切面
     * @author biancl
     * @date 2017-10-12 15:12
     * @param
     * @return void
     */
    @Pointcut("@annotation(com.dataman.gitstats.annotation.AuthRequired)")
    public void authAspect(){

    }

    /**
     * @method: doBefore    前置处理，校验token，token有误时抛出异常
     * @author biancl
     * @date 2017-10-12 18:32
     * @param
     * @return void
     */
    @Before("authAspect()")
    public void doBefore(JoinPoint joinPoint) throws BusinessException{

        log.info("====================进入认证前置拦截器=================");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token=request.getHeader("token");
        if(token==null){
            throw new BusinessException(ApiResultCode.ERR_NOT_AUTH);
        }
        User user=loginService.checkLogin(token);
        request.setAttribute("user",user);
    }


}
