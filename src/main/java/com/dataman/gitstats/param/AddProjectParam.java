package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

public class AddProjectParam {

	@NotNull
	String aid;
	@NotNull
	String name;
	@NotNull
	String[] branchs;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getBranchs() {
		return branchs;
	}
	public void setBranchs(String[] branchs) {
		this.branchs = branchs;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	
	
	
}
