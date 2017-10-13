package com.dataman.gitstats.web;

import com.dataman.gitstats.annotation.AuthRequired;
import com.dataman.gitstats.po.User;
import com.dataman.gitstats.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 登录相关接口
 * @author biancl
 * @date 2017-10-12 20:39
 * @Copyright © 2017北京数人科技有限公司
 */
@RestController
public class LoginController extends BaseController{

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "登录")
    public Object login(String username, String password) throws Exception {
        json.clear();
        User user=loginService.login(username, password);
        setJson(SUCCESS_CODE,user.getToken());
        return json;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ApiOperation(value = "退出登录")
    @AuthRequired
    public Object logout(@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
            HttpServletRequest request) throws Exception {
        json.clear();
        User user= (User) request.getAttribute("user");
        loginService.logout(user);
        setJson(SUCCESS_CODE);
        return json;
    }


}
