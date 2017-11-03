package com.dataman.gitstats.web;

import com.dataman.gitstats.annotation.AuthRequired;
import com.dataman.gitstats.po.User;
import com.dataman.gitstats.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 用户相关接口
 * @author biancl
 * @date 2017-10-12 20:07
 * @Copyright © 2017北京数人科技有限公司
 */
@Api(value = "/user")
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有用户")
//    @AuthRequired
    public Object getAll(){
        return  setJson(SUCCESS_CODE, userService.getAllUser());

    }
    /**
     * @method: add   TODO(参数校验)
     * @Description
     * @author biancl
     * @date 2017-10-12 20:16
     * @param [user]
     * @return java.lang.Object
     */
    @RequestMapping(value = "/",method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    //@AuthRequired
    public Object add(@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
            User user){

        return  setJson(SUCCESS_CODE,userService.addUser(user));

    }

    /**
     * @method: del
     * @Description
     * @author biancl
     * @date 2017-11-03 18:47
     * @param userId
     * @return java.lang.Object
     */
    @RequestMapping(value = "/{userId}",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户")
    @AuthRequired
    public Object del(@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
            @ApiParam(required = true, name = "userId", value = "用户id") @PathVariable String userId){
        userService.del(userId);
        return  setJson(SUCCESS_CODE);
    }


}
