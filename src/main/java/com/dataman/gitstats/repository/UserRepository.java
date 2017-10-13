package com.dataman.gitstats.repository;

import com.dataman.gitstats.po.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: 用户数据管理
 * @author biancl
 * @date 2017-10-12 10:27
 * @Copyright © 2017北京数人科技有限公司
 */
public interface UserRepository  extends MongoRepository <User,String>{
    public User findByUsernameAndPassword(String username,String password);
    public User findByToken(String token);
}
