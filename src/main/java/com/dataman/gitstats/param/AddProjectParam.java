package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "添加统计项目参数")
public class AddProjectParam {

	@ApiModelProperty(required = true, value = "项目名称")
	@NotNull
	String name;
	@ApiModelProperty(required = true, value = "需要统计的版本")
	@NotNull
	String branch;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	
}
