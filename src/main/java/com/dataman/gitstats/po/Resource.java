package com.dataman.gitstats.po;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Description: 资源表，主要是菜单资源
 * @author biancl
 * @date 2017-10-11 18:26
 * @Copyright © 2017北京数人科技有限公司
 */
public class Resource {

    @Id
    private String id;//主键
    private String type;//资源类型  {menu or unknown}
    private String name;//资源名称（菜单名称）
    private String url;//资源访问路径
    private String parentId;//父资源id     父菜单
    private String icon;//图标
    private Integer orderNum;//排序
    private String perms;//权限如read、create、update、delete
    private int status;//状态
    private String creator;
    private Date createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
