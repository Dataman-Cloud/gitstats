package com.dataman.gitstats.service;

import com.dataman.gitstats.exception.ApiResultCode;
import com.dataman.gitstats.exception.BusinessException;
import com.dataman.gitstats.po.User;
import com.dataman.gitstats.repository.UserRepository;
import com.dataman.gitstats.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.Date;
import java.util.List;

/**
 * @Description: 用户相关服务
 * @author biancl
 * @date 2017-10-12 18:49
 * @Copyright © 2017北京数人科技有限公司
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * @method: addUser 添加用户
     * @author biancl
     * @date 2017-10-12 20:05
     * @param [user]
     * @return com.dataman.gitstats.po.User
     */
    public User addUser(User user){
        user.setCreateAt(new Date());
        user.setStatus(0);
        user.setPassword(MD5Util.MD5(user.getPassword()));
        user.setToken(null);
        user.setExpire(null);
        return userRepository.insert(user);
    }
    /**
     * @method: updateUser
     * @Description     修改用户信息
     * @author biancl
     * @date 2017-10-12 20:06
     * @param [user]
     * @return com.dataman.gitstats.po.User
     */
    public User updateUser(User user) throws Exception{
        if(user.getId()==null){
            throw new BusinessException(ApiResultCode.ERR_PARAMETER);
        }
        User oldUser=userRepository.findOne(user.getId());
        if(oldUser==null){
            throw new BusinessException(ApiResultCode.ERR_PARAMETER);
        }
        if(user.getStatus()!=0){
            oldUser.setStatus(user.getStatus());
        }
        if(user.getEmail()!=null){
            oldUser.setEmail(user.getEmail());
        }
        if(user.getMobile()!=null){
            oldUser.setMobile(user.getMobile());
        }
      return  userRepository.save(oldUser);
    }
    /**
     * @method: updatePassword
     * @Description     修改密码
     * @author biancl
     * @date 2017-10-12 20:06
     * @param [userId, oldPassword, newPassword]
     * @return com.dataman.gitstats.po.User
     */
    public User updatePassword(String userId,String oldPassword,String newPassword) throws Exception{

        User oldUser=userRepository.findOne(userId);
        if(oldUser==null){
            throw new BusinessException(ApiResultCode.ERR_PARAMETER);
        }
        if(!MD5Util.MD5(oldPassword).equals(oldUser.getPassword())){
            throw new BusinessException(ApiResultCode.ERR_OLD_PASSWORD);
        }
        oldUser.setPassword(MD5Util.MD5(newPassword));
        return userRepository.save(oldUser);
    }

    public User getUserByUsernameAndPasswrod(String username,String password){
        return userRepository.findByUsernameAndPassword(username,password);
    }

    public User getUserByToken(String token){
        return userRepository.findByToken(token);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }


}
