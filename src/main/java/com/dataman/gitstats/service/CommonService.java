package com.dataman.gitstats.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by biancl on 2017-09-22.
 */
@Service
public class CommonService {
    public String getApplicationUrlByHttpServletRequest(HttpServletRequest request){
        String url=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        return url;
    }

    public String getHookListenerPath(HttpServletRequest request){
        String applicationUrl=getApplicationUrlByHttpServletRequest(request);
        return applicationUrl+"/webHook/listener";
    }
}
