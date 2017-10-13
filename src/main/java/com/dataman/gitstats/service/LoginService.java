package com.dataman.gitstats.service;

import com.dataman.gitstats.exception.ApiResultCode;
import com.dataman.gitstats.exception.BusinessException;
import com.dataman.gitstats.po.User;
import com.dataman.gitstats.repository.UserRepository;
import com.dataman.gitstats.util.MD5Util;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: 登录相关操作
 * @author biancl
 * @date 2017-10-12 16:47
 * @Copyright © 2017北京数人科技有限公司
 */
@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username,String password) throws Exception{
        User user=userRepository.findByUsernameAndPassword(username, MD5Util.MD5(password));
        if(user==null){
            throw new BusinessException(ApiResultCode.ERR_USERNAME_PASSWORD);
        }
        String token= UUID.randomUUID().toString();
        user.setToken(token);

        user.setExpire(System.currentTimeMillis()+1000*60*30);
        userRepository.save(user);
        return user;
    }

    public User checkLogin(String token) throws BusinessException{
        User user=userRepository.findByToken(token);
        if(user==null){
            throw new BusinessException(ApiResultCode.ERR_AUTH_TIMEOUT);
        }
        if(user.getExpire()<System.currentTimeMillis()){
            throw new BusinessException(ApiResultCode.ERR_AUTH_TIMEOUT);
        }else{
            user.setExpire(System.currentTimeMillis()+1000*60*30);
            userRepository.save(user);
        }
        return user;
    }

    public void logout(User user) throws Exception{
        if(user==null){
            throw new BusinessException(ApiResultCode.ERR_AUTH_TIMEOUT);
        }
        user.setToken(null);
        userRepository.save(user);
    }

}
