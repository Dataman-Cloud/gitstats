package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

public class AddProjectParam {

	@NotNull
	String name;
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
