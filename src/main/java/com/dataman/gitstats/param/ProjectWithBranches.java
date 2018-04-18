package com.dataman.gitstats.param;

import java.util.List;

/**
 * @Description: 包含分支列表的项目，用于添加项目组时指定包含/不包含多个项目及分支
 * TODO subgroups在gitlab10.3版本以后才有相关接口，gitlab api中本版本使用的jar包没有提供相应api，暂时不加
 * @author biancl
 * @date 2018-03-04 15:23
 * @Copyright © 2018北京数人科技有限公司
 */
public class ProjectWithBranches {
    private String name;
    private List<String> branches;
//    private List<ProjectWithBranches> subGroups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

}
